# Java Unit Testing Skill Profile

## Skill Overview

**Name**: Java Unit Test Generation  
**Category**: Testing & Quality Assurance  
**Frameworks**: JUnit 5 (Jupiter), JUnit 4, Mockito, AssertJ, Hamcrest  
**Target Audience**: Java developers, QA engineers, automation testers  
**Complexity Level**: Intermediate to Advanced

## Purpose

Generate comprehensive, maintainable unit tests for Java applications following industry best practices, covering standard Java SE, Spring Boot, and enterprise Java patterns.

## Core Competencies

### Testing Frameworks
- **JUnit 5 (Jupiter)**: Modern testing with annotations, parameterized tests, dynamic tests
- **JUnit 4**: Legacy support for existing projects
- **Mockito**: Mocking framework for dependencies and external services
- **AssertJ**: Fluent assertion library for readable tests
- **Hamcrest**: Matcher-based assertions
- **Spring Test**: Integration testing for Spring applications
- **TestNG**: Alternative testing framework with data-driven testing

### Java-Specific Patterns
- **POJO testing**: Plain Java object validation
- **Builder pattern testing**: Complex object construction
- **Exception handling**: Try-catch and expected exceptions
- **Generics and collections**: Type-safe testing
- **Stream API testing**: Functional programming validation
- **Concurrency testing**: Thread-safe code validation
- **Annotation processing**: Custom annotation behavior

## Test Structure & Conventions

### Standard Test Class Structure
```java
package com.example.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Tests")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    void tearDown() {
        // Cleanup resources if needed
    }
    
    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        
        @Test
        @DisplayName("Should create user successfully with valid data")
        void shouldCreateUserSuccessfully() {
            // Arrange
            User user = new User("john@example.com", "John Doe");
            when(userRepository.save(any(User.class))).thenReturn(user);
            
            // Act
            User result = userService.createUser(user);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            verify(userRepository, times(1)).save(user);
        }
    }
}
```

### Naming Conventions

#### Test Class Names
- **Pattern**: `{ClassName}Test` or `{ClassName}Tests`
- **Examples**: `UserServiceTest`, `OrderValidatorTest`

#### Test Method Names
- **Style 1 (Descriptive)**: `shouldDoSomethingWhenCondition()`
- **Style 2 (BDD)**: `givenCondition_whenAction_thenResult()`
- **Style 3 (Simple)**: `testMethodName_scenario()`

**Examples**:
```java
@Test
void shouldReturnUserWhenValidIdProvided()

@Test
void givenValidUser_whenSaving_thenReturnsSavedUser()

@Test
void findById_userExists()
```

## Testing Patterns by Scenario

### 1. Simple Unit Test (No Dependencies)
```java
class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void shouldAddTwoPositiveNumbers() {
        // Arrange
        int a = 5, b = 3;
        
        // Act
        int result = calculator.add(a, b);
        
        // Assert
        assertThat(result).isEqualTo(8);
    }
    
    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "5, 3, 8",
        "-2, 2, 0",
        "0, 0, 0"
    })
    void shouldAddNumbersCorrectly(int a, int b, int expected) {
        assertThat(calculator.add(a, b)).isEqualTo(expected);
    }
}
```

### 2. Testing with Mockito
```java
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private PaymentService paymentService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void shouldProcessOrderSuccessfully() {
        // Arrange
        Order order = new Order(1L, "PENDING");
        Payment payment = new Payment(100.0);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentService.processPayment(any())).thenReturn(true);
        
        // Act
        boolean result = orderService.processOrder(1L);
        
        // Assert
        assertThat(result).isTrue();
        verify(orderRepository).findById(1L);
        verify(paymentService).processPayment(any(Payment.class));
        verify(orderRepository).save(argThat(o -> 
            o.getStatus().equals("COMPLETED")
        ));
    }
}
```

### 3. Exception Testing
```java
class ValidationServiceTest {
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Arrange
        ValidationService service = new ValidationService();
        String invalidEmail = "not-an-email";
        
        // Act & Assert
        assertThatThrownBy(() -> service.validateEmail(invalidEmail))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Invalid email format")
            .hasFieldOrPropertyWithValue("field", "email");
    }
    
    @Test
    void shouldThrowExceptionWithCause() {
        assertThatExceptionOfType(DataAccessException.class)
            .isThrownBy(() -> service.fetchData())
            .withCauseInstanceOf(SQLException.class);
    }
}
```

### 4. Testing Collections and Streams
```java
class UserFilterServiceTest {
    
    @Test
    void shouldFilterActiveUsers() {
        // Arrange
        List<User> users = Arrays.asList(
            new User("john", true),
            new User("jane", false),
            new User("bob", true)
        );
        
        // Act
        List<User> activeUsers = service.filterActiveUsers(users);
        
        // Assert
        assertThat(activeUsers)
            .hasSize(2)
            .extracting(User::getName)
            .containsExactly("john", "bob");
    }
}
```

### 5. Testing with Spring Boot
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUserWhenValidIdProvided() throws Exception {
        // Arrange
        User user = new User(1L, "john@example.com", "John");
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        
        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```

## Best Practices

### ✅ Do This

1. **Use AssertJ for fluent assertions**
```java
assertThat(user)
    .isNotNull()
    .hasFieldOrPropertyWithValue("email", "john@example.com")
    .extracting(User::getName)
    .isEqualTo("John");
```

2. **Use @DisplayName for readable test descriptions**
```java
@Test
@DisplayName("Should calculate discount correctly for premium members")
void testPremiumDiscount() { }
```

3. **Use @Nested for logical grouping**
```java
@Nested
@DisplayName("Happy Path Scenarios")
class HappyPathTests { }

@Nested
@DisplayName("Error Handling")
class ErrorHandlingTests { }
```

4. **Use ArgumentCaptor for complex verifications**
```java
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(userCaptor.capture());
assertThat(userCaptor.getValue().getEmail()).isEqualTo("test@example.com");
```

5. **Use @ParameterizedTest for multiple scenarios**
```java
@ParameterizedTest
@ValueSource(strings = {"", "  ", "\t", "\n"})
void shouldRejectBlankInput(String input) {
    assertThatThrownBy(() -> service.process(input))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### ❌ Don't Do This

1. **Don't test private methods directly**
```java
// Bad
void testPrivateMethod() {
    Method method = MyClass.class.getDeclaredMethod("privateMethod");
    method.setAccessible(true);
    // ...
}

// Good - Test through public interface
void shouldProcessDataCorrectly() {
    service.processData(input); // This internally calls private methods
}
```

2. **Don't ignore test isolation**
```java
// Bad - Shared mutable state
static List<User> users = new ArrayList<>();

@Test
void test1() {
    users.add(new User("john"));
}

@Test
void test2() {
    // Depends on test1 execution order
    assertThat(users).hasSize(1);
}
```

3. **Don't use Thread.sleep() in tests**
```java
// Bad
@Test
void testAsync() throws InterruptedException {
    service.processAsync();
    Thread.sleep(1000); // Flaky!
    verify(service).callback();
}

// Good - Use proper async testing utilities
@Test
void testAsync() {
    CompletableFuture<Void> future = service.processAsync();
    assertThat(future).succeedsWithin(Duration.ofSeconds(1));
}
```

## Advanced Patterns

### Testing Builder Pattern
```java
@Test
void shouldBuildUserWithAllFields() {
    User user = User.builder()
        .id(1L)
        .email("john@example.com")
        .name("John Doe")
        .age(30)
        .active(true)
        .build();
    
    assertThat(user)
        .hasFieldOrPropertyWithValue("id", 1L)
        .hasFieldOrPropertyWithValue("email", "john@example.com")
        .hasFieldOrPropertyWithValue("name", "John Doe")
        .hasFieldOrPropertyWithValue("age", 30)
        .hasFieldOrPropertyWithValue("active", true);
}
```

### Testing with Test Containers (Database)
```java
@Testcontainers
@SpringBootTest
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User("john@example.com", "John");
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
    }
}
```

### Testing Asynchronous Code
```java
@Test
void shouldProcessAsynchronously() {
    // Using CompletableFuture
    CompletableFuture<String> future = service.processAsync();
    
    assertThat(future)
        .succeedsWithin(Duration.ofSeconds(2))
        .isEqualTo("processed");
}

@Test
void shouldHandleAsyncException() {
    CompletableFuture<String> future = service.processAsyncWithError();
    
    assertThat(future)
        .failsWithin(Duration.ofSeconds(2))
        .withThrowableOfType(ExecutionException.class)
        .havingCause()
        .isInstanceOf(ProcessingException.class);
}
```

## Code Coverage Goals

### Minimum Standards
- **Line Coverage**: 80%+
- **Branch Coverage**: 75%+
- **Method Coverage**: 90%+

### Focus Areas
1. **Business Logic**: 95%+ coverage
2. **Utility Classes**: 90%+ coverage
3. **Controllers/REST APIs**: 85%+ coverage
4. **Configuration Classes**: 60%+ coverage (test critical paths)

### Exclusions
- Generated code (Lombok, MapStruct)
- POJOs (getters/setters only)
- Main application class
- Constants and enums (unless complex logic)

## Common Java Testing Scenarios

### 1. Testing Equals and HashCode
```java
@Test
void shouldHaveCorrectEqualsAndHashCode() {
    User user1 = new User(1L, "john@example.com");
    User user2 = new User(1L, "john@example.com");
    User user3 = new User(2L, "jane@example.com");
    
    assertThat(user1)
        .isEqualTo(user2)
        .hasSameHashCodeAs(user2)
        .isNotEqualTo(user3);
}
```

### 2. Testing Serialization
```java
@Test
void shouldSerializeAndDeserialize() throws Exception {
    User user = new User(1L, "john@example.com");
    
    // Serialize
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(user);
    
    // Deserialize
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    User deserialized = (User) ois.readObject();
    
    assertThat(deserialized).isEqualTo(user);
}
```

### 3. Testing Enums
```java
@Test
void shouldHaveAllExpectedStatuses() {
    assertThat(OrderStatus.values())
        .hasSize(4)
        .contains(OrderStatus.PENDING, OrderStatus.COMPLETED, 
                  OrderStatus.CANCELLED, OrderStatus.REFUNDED);
}

@Test
void shouldConvertFromString() {
    OrderStatus status = OrderStatus.valueOf("PENDING");
    assertThat(status).isEqualTo(OrderStatus.PENDING);
}
```

## Maven/Gradle Dependencies

### Maven (pom.xml)
```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Gradle (build.gradle)
```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.assertj:assertj-core:3.24.2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## Examples Directory

See the `/examples` directory for complete, runnable examples:
- [Basic Unit Tests](./examples/BasicUnitTestExample.java)
- [Mockito Examples](./examples/MockitoTestExample.java)
- [Spring Boot Tests](./examples/SpringBootTestExample.java)
- [Parameterized Tests](./examples/ParameterizedTestExample.java)
- [Exception Testing](./examples/ExceptionTestExample.java)

## Quick Reference

### Common Annotations
| Annotation | Purpose |
|------------|---------|
| `@Test` | Marks a test method |
| `@BeforeEach` | Runs before each test |
| `@AfterEach` | Runs after each test |
| `@BeforeAll` | Runs once before all tests (static) |
| `@AfterAll` | Runs once after all tests (static) |
| `@DisplayName` | Readable test description |
| `@Nested` | Grouped test classes |
| `@ParameterizedTest` | Test with multiple inputs |
| `@Mock` | Creates a mock object |
| `@InjectMocks` | Injects mocks into tested object |
| `@Disabled` | Temporarily disable test |
| `@Tag` | Tag tests for selective execution |

### Common Assertions (AssertJ)
```java
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotNull();
assertThat(list).hasSize(3);
assertThat(list).contains("item");
assertThat(string).startsWith("prefix");
assertThat(exception).hasMessage("error");
```

### Common Mockito Methods
```java
when(mock.method()).thenReturn(value);
when(mock.method()).thenThrow(exception);
verify(mock).method();
verify(mock, times(2)).method();
verify(mock, never()).method();
```

## Integration with CI/CD

### Maven Surefire Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.3</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

### JaCoCo Coverage
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

**Last Updated**: December 27, 2025  
**Status**: Production Ready  
**Maintainer**: GitHub Copilot Playbook Team
