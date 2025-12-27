package com.example.exception;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import java.io.*;
import java.util.*;

/**
 * Exception Testing Example
 * Demonstrates various approaches to testing exceptions in Java
 */
@DisplayName("Exception Testing Examples")
class ExceptionTestExamples {
    
    @Nested
    @DisplayName("Basic Exception Testing")
    class BasicExceptionTests {
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for null input")
        void shouldThrowIllegalArgumentException() {
            UserValidator validator = new UserValidator();
            
            assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception with specific message")
        void shouldThrowExceptionWithMessage() {
            OrderService service = new OrderService();
            
            assertThatThrownBy(() -> service.processOrder(-1L))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Invalid order ID: -1")
                .hasNoCause();
        }
        
        @Test
        @DisplayName("Should throw exception containing message")
        void shouldThrowExceptionContainingMessage() {
            PaymentService service = new PaymentService();
            
            assertThatThrownBy(() -> service.charge(-100))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("negative")
                .hasMessageContaining("amount");
        }
    }
    
    @Nested
    @DisplayName("Exception Type Testing")
    class ExceptionTypeTests {
        
        @Test
        @DisplayName("Should throw specific exception type")
        void shouldThrowSpecificExceptionType() {
            FileProcessor processor = new FileProcessor();
            
            assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> processor.readFile("nonexistent.txt"))
                .withMessageContaining("nonexistent.txt");
        }
        
        @Test
        @DisplayName("Should throw runtime exception")
        void shouldThrowRuntimeException() {
            DataService service = new DataService();
            
            assertThatRuntimeException()
                .isThrownBy(() -> service.fetchData(null))
                .withMessage("Data source cannot be null");
        }
        
        @Test
        @DisplayName("Should throw IOException")
        void shouldThrowIOException() {
            NetworkService service = new NetworkService();
            
            assertThatIOException()
                .isThrownBy(() -> service.connect("invalid-host"))
                .withMessageContaining("connection failed");
        }
    }
    
    @Nested
    @DisplayName("Exception Cause Testing")
    class ExceptionCauseTests {
        
        @Test
        @DisplayName("Should have specific cause")
        void shouldHaveSpecificCause() {
            DatabaseService service = new DatabaseService();
            
            assertThatThrownBy(() -> service.executeQuery("SELECT * FROM invalid"))
                .isInstanceOf(DataAccessException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("Table 'invalid' doesn't exist");
        }
        
        @Test
        @DisplayName("Should have nested causes")
        void shouldHaveNestedCauses() {
            ApplicationService service = new ApplicationService();
            
            assertThatThrownBy(() -> service.performComplexOperation())
                .isInstanceOf(ApplicationException.class)
                .hasCauseInstanceOf(ServiceException.class)
                .hasRootCauseInstanceOf(IOException.class);
        }
        
        @Test
        @DisplayName("Should not have cause")
        void shouldNotHaveCause() {
            ValidationService service = new ValidationService();
            
            assertThatThrownBy(() -> service.validateInput(""))
                .isInstanceOf(ValidationException.class)
                .hasNoCause();
        }
    }
    
    @Nested
    @DisplayName("Exception Field Testing")
    class ExceptionFieldTests {
        
        @Test
        @DisplayName("Should have specific field values")
        void shouldHaveSpecificFieldValues() {
            AccountService service = new AccountService();
            
            assertThatThrownBy(() -> service.withdraw(1000))
                .isInstanceOf(InsufficientFundsException.class)
                .hasFieldOrPropertyWithValue("requestedAmount", 1000.0)
                .hasFieldOrPropertyWithValue("availableAmount", 500.0)
                .hasMessage("Insufficient funds: requested 1000.0, available 500.0");
        }
        
        @Test
        @DisplayName("Should contain error code")
        void shouldContainErrorCode() {
            ApiService service = new ApiService();
            
            assertThatThrownBy(() -> service.callApi())
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertThat(apiEx.getErrorCode()).isEqualTo("API_ERROR_001");
                    assertThat(apiEx.getStatusCode()).isEqualTo(500);
                });
        }
    }
    
    @Nested
    @DisplayName("Multiple Exceptions Testing")
    class MultipleExceptionsTests {
        
        @Test
        @DisplayName("Should throw one of multiple possible exceptions")
        void shouldThrowOneOfMultipleExceptions() {
            RandomService service = new RandomService();
            
            assertThatThrownBy(() -> service.randomOperation())
                .isInstanceOfAny(
                    IllegalStateException.class,
                    IllegalArgumentException.class,
                    RuntimeException.class
                );
        }
        
        @Test
        @DisplayName("Should handle different exceptions in try-catch")
        void shouldHandleDifferentExceptions() {
            RiskyService service = new RiskyService();
            
            // Test IOException
            assertThatThrownBy(() -> service.operation("io-error"))
                .isInstanceOf(IOException.class);
            
            // Test IllegalArgumentException
            assertThatThrownBy(() -> service.operation("illegal-arg"))
                .isInstanceOf(IllegalArgumentException.class);
            
            // Test RuntimeException
            assertThatThrownBy(() -> service.operation("runtime-error"))
                .isInstanceOf(RuntimeException.class);
        }
    }
    
    @Nested
    @DisplayName("Exception Not Thrown Testing")
    class ExceptionNotThrownTests {
        
        @Test
        @DisplayName("Should not throw exception for valid input")
        void shouldNotThrowExceptionForValidInput() {
            UserValidator validator = new UserValidator();
            User validUser = new User("john@example.com", "John Doe");
            
            assertThatCode(() -> validator.validate(validUser))
                .doesNotThrowAnyException();
        }
        
        @Test
        @DisplayName("Should complete successfully without exception")
        void shouldCompleteSuccessfully() {
            OrderService service = new OrderService();
            
            assertThatNoException()
                .isThrownBy(() -> service.processOrder(123L));
        }
    }
    
    @Nested
    @DisplayName("Custom Exception Testing")
    class CustomExceptionTests {
        
        @Test
        @DisplayName("Should throw custom business exception")
        void shouldThrowCustomBusinessException() {
            InventoryService service = new InventoryService();
            
            assertThatThrownBy(() -> service.reserveItem("OUT_OF_STOCK_ITEM"))
                .isInstanceOf(OutOfStockException.class)
                .hasMessage("Item is out of stock: OUT_OF_STOCK_ITEM")
                .satisfies(ex -> {
                    OutOfStockException stockEx = (OutOfStockException) ex;
                    assertThat(stockEx.getItemCode()).isEqualTo("OUT_OF_STOCK_ITEM");
                    assertThat(stockEx.getAvailableQuantity()).isZero();
                });
        }
        
        @Test
        @DisplayName("Should throw chained custom exceptions")
        void shouldThrowChainedCustomExceptions() {
            OrderProcessingService service = new OrderProcessingService();
            
            assertThatThrownBy(() -> service.fulfillOrder("INVALID"))
                .isInstanceOf(OrderFulfillmentException.class)
                .hasCauseInstanceOf(InventoryException.class)
                .hasMessageContaining("Cannot fulfill order");
        }
    }
}

// Service classes with exception scenarios
class UserValidator {
    public void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}

class OrderService {
    public void processOrder(Long orderId) {
        if (orderId == null || orderId < 0) {
            throw new InvalidOrderException("Invalid order ID: " + orderId);
        }
        // Process order
    }
}

class PaymentService {
    public void charge(double amount) {
        if (amount < 0) {
            throw new PaymentException("Cannot charge negative amount: " + amount);
        }
    }
}

class FileProcessor {
    public String readFile(String filename) throws FileNotFoundException {
        if (!new File(filename).exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        return "";
    }
}

class DataService {
    public void fetchData(String source) {
        if (source == null) {
            throw new RuntimeException("Data source cannot be null");
        }
    }
}

class NetworkService {
    public void connect(String host) throws IOException {
        if (host.equals("invalid-host")) {
            throw new IOException("Network connection failed for host: " + host);
        }
    }
}

class DatabaseService {
    public void executeQuery(String query) {
        throw new DataAccessException("Query execution failed", 
            new SQLException("Table 'invalid' doesn't exist"));
    }
}

class ApplicationService {
    public void performComplexOperation() {
        throw new ApplicationException("Complex operation failed",
            new ServiceException("Service error",
                new IOException("IO error")));
    }
}

class ValidationService {
    public void validateInput(String input) {
        if (input == null || input.isEmpty()) {
            throw new ValidationException("Input cannot be empty");
        }
    }
}

class AccountService {
    public void withdraw(double amount) {
        throw new InsufficientFundsException(amount, 500.0);
    }
}

class ApiService {
    public void callApi() {
        throw new ApiException("API call failed", "API_ERROR_001", 500);
    }
}

class InventoryService {
    public void reserveItem(String itemCode) {
        throw new OutOfStockException(itemCode, 0);
    }
}

class OrderProcessingService {
    public void fulfillOrder(String orderId) {
        throw new OrderFulfillmentException("Cannot fulfill order: " + orderId,
            new InventoryException("Inventory error"));
    }
}

// Custom exception classes
class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }
}

class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}

class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

class SQLException extends Exception {
    public SQLException(String message) {
        super(message);
    }
}

class ApplicationException extends RuntimeException {
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends RuntimeException {
    private final double requestedAmount;
    private final double availableAmount;
    
    public InsufficientFundsException(double requested, double available) {
        super(String.format("Insufficient funds: requested %.1f, available %.1f", requested, available));
        this.requestedAmount = requested;
        this.availableAmount = available;
    }
    
    public double getRequestedAmount() { return requestedAmount; }
    public double getAvailableAmount() { return availableAmount; }
}

class ApiException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;
    
    public ApiException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public String getErrorCode() { return errorCode; }
    public int getStatusCode() { return statusCode; }
}

class OutOfStockException extends RuntimeException {
    private final String itemCode;
    private final int availableQuantity;
    
    public OutOfStockException(String itemCode, int availableQuantity) {
        super("Item is out of stock: " + itemCode);
        this.itemCode = itemCode;
        this.availableQuantity = availableQuantity;
    }
    
    public String getItemCode() { return itemCode; }
    public int getAvailableQuantity() { return availableQuantity; }
}

class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }
}

class OrderFulfillmentException extends RuntimeException {
    public OrderFulfillmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

class User {
    private String email;
    private String name;
    
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    public String getEmail() { return email; }
    public String getName() { return name; }
}

class RandomService {
    public void randomOperation() {
        throw new IllegalStateException("Random error");
    }
}

class RiskyService {
    public void operation(String scenario) throws IOException {
        switch (scenario) {
            case "io-error":
                throw new IOException("IO error");
            case "illegal-arg":
                throw new IllegalArgumentException("Illegal argument");
            case "runtime-error":
                throw new RuntimeException("Runtime error");
            default:
                // Normal operation
        }
    }
}
