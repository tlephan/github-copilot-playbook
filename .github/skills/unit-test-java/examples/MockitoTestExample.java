package com.example.mockito;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito Testing Example
 * Demonstrates mocking dependencies and verifying interactions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Mockito Tests")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private AuditLogger auditLogger;
    
    @InjectMocks
    private UserService userService;
    
    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            // Arrange
            User user = new User(null, "john@example.com", "John Doe");
            User savedUser = new User(1L, "john@example.com", "John Doe");
            
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            
            // Act
            User result = userService.createUser(user);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            
            verify(userRepository).existsByEmail("john@example.com");
            verify(userRepository).save(user);
            verify(emailService).sendWelcomeEmail("john@example.com");
            verify(auditLogger).logUserCreation(1L);
        }
        
        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            User user = new User(null, "john@example.com", "John Doe");
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
            
            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already registered: john@example.com");
            
            verify(userRepository).existsByEmail("john@example.com");
            verify(userRepository, never()).save(any(User.class));
            verify(emailService, never()).sendWelcomeEmail(anyString());
        }
        
        @Test
        @DisplayName("Should handle null email")
        void shouldHandleNullEmail() {
            User user = new User(null, null, "John Doe");
            
            assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be null");
            
            verifyNoInteractions(userRepository, emailService, auditLogger);
        }
    }
    
    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {
        
        @Test
        @DisplayName("Should find user by id")
        void shouldFindUserById() {
            // Arrange
            User user = new User(1L, "john@example.com", "John Doe");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            
            // Act
            Optional<User> result = userService.findById(1L);
            
            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("john@example.com");
            verify(userRepository).findById(1L);
        }
        
        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            Optional<User> result = userService.findById(999L);
            
            assertThat(result).isEmpty();
            verify(userRepository).findById(999L);
        }
        
        @Test
        @DisplayName("Should find all active users")
        void shouldFindAllActiveUsers() {
            // Arrange
            List<User> users = Arrays.asList(
                new User(1L, "john@example.com", "John Doe"),
                new User(2L, "jane@example.com", "Jane Smith")
            );
            when(userRepository.findByActiveTrue()).thenReturn(users);
            
            // Act
            List<User> result = userService.findActiveUsers();
            
            // Assert
            assertThat(result).hasSize(2);
            verify(userRepository).findByActiveTrue();
        }
    }
    
    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {
        
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Arrange
            User existingUser = new User(1L, "john@example.com", "John Doe");
            User updatedUser = new User(1L, "john@example.com", "John Smith");
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            
            // Act
            User result = userService.updateUser(1L, "John Smith");
            
            // Assert
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getName()).isEqualTo("John Smith");
            assertThat(result.getName()).isEqualTo("John Smith");
        }
    }
    
    @Nested
    @DisplayName("User Deletion Tests")
    class UserDeletionTests {
        
        @Test
        @DisplayName("Should delete user and send notification")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            User user = new User(1L, "john@example.com", "John Doe");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            doNothing().when(userRepository).deleteById(1L);
            
            // Act
            userService.deleteUser(1L);
            
            // Assert
            verify(userRepository).findById(1L);
            verify(userRepository).deleteById(1L);
            verify(emailService).sendAccountClosureEmail("john@example.com");
            verify(auditLogger).logUserDeletion(1L);
        }
        
        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            
            assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: 999");
            
            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}

// Domain classes
class User {
    private Long id;
    private String email;
    private String name;
    
    public User(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
    
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// Service class under test
class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditLogger auditLogger;
    
    public UserService(UserRepository userRepository, EmailService emailService, AuditLogger auditLogger) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.auditLogger = auditLogger;
    }
    
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + user.getEmail());
        }
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail());
        auditLogger.logUserCreation(savedUser.getId());
        return savedUser;
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> findActiveUsers() {
        return userRepository.findByActiveTrue();
    }
    
    public User updateUser(Long id, String newName) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        user.setName(newName);
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        userRepository.deleteById(id);
        emailService.sendAccountClosureEmail(user.getEmail());
        auditLogger.logUserDeletion(id);
    }
}

// Dependencies (interfaces)
interface UserRepository {
    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findByActiveTrue();
    void deleteById(Long id);
}

interface EmailService {
    void sendWelcomeEmail(String email);
    void sendAccountClosureEmail(String email);
}

interface AuditLogger {
    void logUserCreation(Long userId);
    void logUserDeletion(Long userId);
}

// Custom exceptions
class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
