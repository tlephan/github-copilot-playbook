package com.example.basic;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Basic Unit Test Example
 * Demonstrates simple unit testing without external dependencies
 */
@DisplayName("Calculator Basic Tests")
class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @AfterEach
    void tearDown() {
        calculator = null;
    }
    
    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {
        
        @Test
        @DisplayName("Should add two positive numbers")
        void shouldAddPositiveNumbers() {
            // Arrange
            int a = 5;
            int b = 3;
            
            // Act
            int result = calculator.add(a, b);
            
            // Assert
            assertThat(result).isEqualTo(8);
        }
        
        @Test
        @DisplayName("Should add negative numbers")
        void shouldAddNegativeNumbers() {
            assertThat(calculator.add(-5, -3)).isEqualTo(-8);
        }
        
        @Test
        @DisplayName("Should add positive and negative numbers")
        void shouldAddMixedNumbers() {
            assertThat(calculator.add(5, -3)).isEqualTo(2);
        }
        
        @Test
        @DisplayName("Should return zero when adding zero")
        void shouldHandleZero() {
            assertThat(calculator.add(0, 0)).isEqualTo(0);
            assertThat(calculator.add(5, 0)).isEqualTo(5);
        }
    }
    
    @Nested
    @DisplayName("Subtraction Tests")
    class SubtractionTests {
        
        @Test
        void shouldSubtractPositiveNumbers() {
            assertThat(calculator.subtract(10, 3)).isEqualTo(7);
        }
        
        @Test
        void shouldSubtractNegativeNumbers() {
            assertThat(calculator.subtract(-5, -3)).isEqualTo(-2);
        }
    }
    
    @Nested
    @DisplayName("Multiplication Tests")
    class MultiplicationTests {
        
        @Test
        void shouldMultiplyPositiveNumbers() {
            assertThat(calculator.multiply(5, 3)).isEqualTo(15);
        }
        
        @Test
        void shouldReturnZeroWhenMultiplyingByZero() {
            assertThat(calculator.multiply(5, 0)).isEqualTo(0);
        }
        
        @Test
        void shouldHandleNegativeMultiplication() {
            assertThat(calculator.multiply(-5, 3)).isEqualTo(-15);
            assertThat(calculator.multiply(-5, -3)).isEqualTo(15);
        }
    }
    
    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {
        
        @Test
        void shouldDividePositiveNumbers() {
            assertThat(calculator.divide(10, 2)).isEqualTo(5.0);
        }
        
        @Test
        void shouldHandleDecimalResults() {
            assertThat(calculator.divide(10, 3))
                .isCloseTo(3.333, within(0.001));
        }
        
        @Test
        @DisplayName("Should throw exception when dividing by zero")
        void shouldThrowExceptionForDivisionByZero() {
            assertThatThrownBy(() -> calculator.divide(10, 0))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Cannot divide by zero");
        }
    }
}

// Class under test
class Calculator {
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return (double) a / b;
    }
}
