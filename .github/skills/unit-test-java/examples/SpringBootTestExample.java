package com.example.springboot;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Spring Boot Testing Example
 * Demonstrates testing Spring Boot REST controllers with MockMvc
 */
@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserTests {
        
        @Test
        @DisplayName("Should return user when valid id provided")
        void shouldReturnUserWhenValidId() throws Exception {
            // Arrange
            Long userId = 1L;
            User user = new User(userId, "john@example.com", "John Doe");
            when(userService.findById(userId)).thenReturn(Optional.of(user));
            
            // Act & Assert
            mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
            
            verify(userService, times(1)).findById(userId);
        }
        
        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            Long userId = 999L;
            when(userService.findById(userId)).thenReturn(Optional.empty());
            
            mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
            
            verify(userService).findById(userId);
        }
        
        @Test
        @DisplayName("Should return 400 for invalid id format")
        void shouldReturn400ForInvalidId() throws Exception {
            mockMvc.perform(get("/api/users/{id}", "invalid"))
                .andExpect(status().isBadRequest());
            
            verifyNoInteractions(userService);
        }
    }
    
    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsersTests {
        
        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() throws Exception {
            List<User> users = Arrays.asList(
                new User(1L, "john@example.com", "John Doe"),
                new User(2L, "jane@example.com", "Jane Smith")
            );
            when(userService.findAll()).thenReturn(users);
            
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
        }
        
        @Test
        @DisplayName("Should return empty array when no users")
        void shouldReturnEmptyArrayWhenNoUsers() throws Exception {
            when(userService.findAll()).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }
        
        @Test
        @DisplayName("Should support pagination")
        void shouldSupportPagination() throws Exception {
            List<User> users = Collections.singletonList(
                new User(1L, "john@example.com", "John Doe")
            );
            when(userService.findAll(0, 10)).thenReturn(users);
            
            mockMvc.perform(get("/api/users")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        }
    }
    
    @Nested
    @DisplayName("POST /api/users")
    class CreateUserTests {
        
        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            User inputUser = new User(null, "john@example.com", "John Doe");
            User savedUser = new User(1L, "john@example.com", "John Doe");
            
            when(userService.createUser(any(User.class))).thenReturn(savedUser);
            
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"john@example.com\",\"name\":\"John Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
            
            verify(userService).createUser(any(User.class));
        }
        
        @Test
        @DisplayName("Should return 400 for invalid email")
        void shouldReturn400ForInvalidEmail() throws Exception {
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"invalid-email\",\"name\":\"John Doe\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());
            
            verifyNoInteractions(userService);
        }
        
        @Test
        @DisplayName("Should return 400 when email is missing")
        void shouldReturn400WhenEmailMissing() throws Exception {
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"John Doe\"}"))
                .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailExists() throws Exception {
            when(userService.createUser(any(User.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already registered"));
            
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"john@example.com\",\"name\":\"John Doe\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
        }
    }
    
    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUserTests {
        
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            Long userId = 1L;
            User updatedUser = new User(userId, "john@example.com", "John Smith");
            
            when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);
            
            mockMvc.perform(put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"john@example.com\",\"name\":\"John Smith\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"));
            
            verify(userService).updateUser(eq(userId), any(User.class));
        }
        
        @Test
        @DisplayName("Should return 404 when updating non-existent user")
        void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
            Long userId = 999L;
            when(userService.updateUser(eq(userId), any(User.class)))
                .thenThrow(new UserNotFoundException("User not found"));
            
            mockMvc.perform(put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"john@example.com\",\"name\":\"John Doe\"}"))
                .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUserTests {
        
        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            Long userId = 1L;
            doNothing().when(userService).deleteUser(userId);
            
            mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
            
            verify(userService).deleteUser(userId);
        }
        
        @Test
        @DisplayName("Should return 404 when deleting non-existent user")
        void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
            Long userId = 999L;
            doThrow(new UserNotFoundException("User not found"))
                .when(userService).deleteUser(userId);
            
            mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
        }
    }
    
    @Nested
    @DisplayName("GET /api/users/search")
    class SearchUsersTests {
        
        @Test
        @DisplayName("Should search users by name")
        void shouldSearchUsersByName() throws Exception {
            List<User> users = Collections.singletonList(
                new User(1L, "john@example.com", "John Doe")
            );
            when(userService.searchByName("John")).thenReturn(users);
            
            mockMvc.perform(get("/api/users/search")
                    .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
        }
        
        @Test
        @DisplayName("Should return empty array when no matches")
        void shouldReturnEmptyArrayWhenNoMatches() throws Exception {
            when(userService.searchByName("NonExistent")).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/api/users/search")
                    .param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}

// Mock controller for testing
class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
}

// Mock service interface
interface UserService {
    Optional<User> findById(Long id);
    List<User> findAll();
    List<User> findAll(int page, int size);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> searchByName(String name);
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
}

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
