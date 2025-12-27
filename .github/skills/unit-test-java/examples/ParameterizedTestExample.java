package com.example.parameterized;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;

/**
 * Parameterized Test Example
 * Demonstrates various ways to provide test data for parameterized tests
 */
@DisplayName("Parameterized Test Examples")
class ParameterizedTestExamples {
    
    @Nested
    @DisplayName("Value Source Tests")
    class ValueSourceTests {
        
        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        @DisplayName("Should reject blank strings")
        void shouldRejectBlankStrings(String input) {
            assertThat(StringValidator.isValid(input)).isFalse();
        }
        
        @ParameterizedTest
        @ValueSource(ints = {1, 3, 5, 7, 9})
        @DisplayName("Should identify odd numbers")
        void shouldIdentifyOddNumbers(int number) {
            assertThat(number % 2).isEqualTo(1);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {0.0, 0.1, 1.0, 99.99})
        @DisplayName("Should accept valid percentages")
        void shouldAcceptValidPercentages(double percentage) {
            assertThat(PercentageValidator.isValid(percentage)).isTrue();
        }
    }
    
    @Nested
    @DisplayName("CSV Source Tests")
    class CsvSourceTests {
        
        @ParameterizedTest
        @CsvSource({
            "1, 1, 2",
            "5, 3, 8",
            "10, -5, 5",
            "0, 0, 0",
            "-2, -3, -5"
        })
        @DisplayName("Should add numbers correctly")
        void shouldAddNumbers(int a, int b, int expected) {
            Calculator calc = new Calculator();
            assertThat(calc.add(a, b)).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "john@example.com, true",
            "invalid-email, false",
            "test@test, false",
            "user@domain.co.uk, true",
            "@example.com, false"
        })
        @DisplayName("Should validate email format")
        void shouldValidateEmailFormat(String email, boolean expected) {
            assertThat(EmailValidator.isValid(email)).isEqualTo(expected);
        }
    }
    
    @Nested
    @DisplayName("CSV File Source Tests")
    class CsvFileSourceTests {
        
        @ParameterizedTest
        @CsvFileSource(resources = "/test-data/users.csv", numLinesToSkip = 1)
        @DisplayName("Should validate users from CSV file")
        void shouldValidateUsersFromFile(String name, int age, String email) {
            User user = new User(name, age, email);
            assertThat(user.isValid()).isTrue();
            assertThat(user.getAge()).isGreaterThan(0);
        }
    }
    
    @Nested
    @DisplayName("Method Source Tests")
    class MethodSourceTests {
        
        @ParameterizedTest
        @MethodSource("provideStringsForUpperCase")
        @DisplayName("Should convert to uppercase")
        void shouldConvertToUpperCase(String input, String expected) {
            assertThat(input.toUpperCase()).isEqualTo(expected);
        }
        
        static Stream<Arguments> provideStringsForUpperCase() {
            return Stream.of(
                Arguments.of("hello", "HELLO"),
                Arguments.of("world", "WORLD"),
                Arguments.of("JUnit", "JUNIT"),
                Arguments.of("", "")
            );
        }
        
        @ParameterizedTest
        @MethodSource("provideUsersForValidation")
        @DisplayName("Should validate user objects")
        void shouldValidateUserObjects(User user, boolean expectedValid) {
            assertThat(user.isValid()).isEqualTo(expectedValid);
        }
        
        static Stream<Arguments> provideUsersForValidation() {
            return Stream.of(
                Arguments.of(new User("John", 25, "john@example.com"), true),
                Arguments.of(new User("", 25, "john@example.com"), false),
                Arguments.of(new User("Jane", -5, "jane@example.com"), false),
                Arguments.of(new User("Bob", 30, "invalid-email"), false),
                Arguments.of(new User("Alice", 35, "alice@example.com"), true)
            );
        }
    }
    
    @Nested
    @DisplayName("Enum Source Tests")
    class EnumSourceTests {
        
        @ParameterizedTest
        @EnumSource(OrderStatus.class)
        @DisplayName("Should handle all order statuses")
        void shouldHandleAllOrderStatuses(OrderStatus status) {
            assertThat(status).isNotNull();
            assertThat(status.name()).isNotEmpty();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"COMPLETED", "CANCELLED"})
        @DisplayName("Should identify terminal statuses")
        void shouldIdentifyTerminalStatuses(OrderStatus status) {
            assertThat(status.isTerminal()).isTrue();
        }
        
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PENDING"})
        @DisplayName("Should exclude pending status")
        void shouldExcludePendingStatus(OrderStatus status) {
            assertThat(status).isNotEqualTo(OrderStatus.PENDING);
        }
    }
    
    @Nested
    @DisplayName("Arguments Source Tests")
    class ArgumentsSourceTests {
        
        @ParameterizedTest
        @ArgumentsSource(DiscountArgumentsProvider.class)
        @DisplayName("Should calculate discount correctly")
        void shouldCalculateDiscount(double originalPrice, double discountPercent, double expectedPrice) {
            PriceCalculator calculator = new PriceCalculator();
            double result = calculator.applyDiscount(originalPrice, discountPercent);
            assertThat(result).isCloseTo(expectedPrice, within(0.01));
        }
    }
    
    @Nested
    @DisplayName("Null and Empty Source Tests")
    class NullAndEmptySourceTests {
        
        @ParameterizedTest
        @NullSource
        @DisplayName("Should handle null input")
        void shouldHandleNullInput(String input) {
            assertThat(StringValidator.isValid(input)).isFalse();
        }
        
        @ParameterizedTest
        @EmptySource
        @DisplayName("Should handle empty string")
        void shouldHandleEmptyString(String input) {
            assertThat(input).isEmpty();
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should handle null and empty")
        void shouldHandleNullAndEmpty(String input) {
            assertThat(StringValidator.isValid(input)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Complex Object Tests")
    class ComplexObjectTests {
        
        @ParameterizedTest
        @MethodSource("provideProductsForPriceCalculation")
        @DisplayName("Should calculate total price with tax")
        void shouldCalculateTotalPriceWithTax(Product product, double taxRate, double expectedTotal) {
            double total = product.calculateTotalPrice(taxRate);
            assertThat(total).isCloseTo(expectedTotal, within(0.01));
        }
        
        static Stream<Arguments> provideProductsForPriceCalculation() {
            return Stream.of(
                Arguments.of(new Product("Item1", 100.0, 2), 0.1, 220.0),
                Arguments.of(new Product("Item2", 50.0, 1), 0.2, 60.0),
                Arguments.of(new Product("Item3", 25.0, 4), 0.15, 115.0)
            );
        }
    }
}

// Helper classes
class StringValidator {
    public static boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }
}

class PercentageValidator {
    public static boolean isValid(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}

class EmailValidator {
    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}

class User {
    private String name;
    private int age;
    private String email;
    
    public User(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    public boolean isValid() {
        return name != null && !name.isEmpty() 
            && age > 0 
            && EmailValidator.isValid(email);
    }
    
    public int getAge() { return age; }
}

enum OrderStatus {
    PENDING(false),
    PROCESSING(false),
    COMPLETED(true),
    CANCELLED(true),
    REFUNDED(true);
    
    private final boolean terminal;
    
    OrderStatus(boolean terminal) {
        this.terminal = terminal;
    }
    
    public boolean isTerminal() {
        return terminal;
    }
}

class PriceCalculator {
    public double applyDiscount(double originalPrice, double discountPercent) {
        return originalPrice * (1 - discountPercent / 100);
    }
}

class Product {
    private String name;
    private double price;
    private int quantity;
    
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public double calculateTotalPrice(double taxRate) {
        double subtotal = price * quantity;
        return subtotal * (1 + taxRate);
    }
}

// Custom ArgumentsProvider
class DiscountArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(100.0, 10.0, 90.0),
            Arguments.of(200.0, 25.0, 150.0),
            Arguments.of(50.0, 50.0, 25.0),
            Arguments.of(75.0, 20.0, 60.0)
        );
    }
}
