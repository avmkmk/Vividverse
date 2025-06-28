package com.vividverse.user.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    @Test
    public void testPasswordContainsUsername() {
        // Test cases where password contains username
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("john123", "john");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("mypassword123", "password");
        });
    }

    @Test
    public void testPasswordContainsUsernamePart() {
        // Test cases where password contains parts of username
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("joh123", "john");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("admin123", "administrator");
        });
    }

    @Test
    public void testPasswordContainsReversedUsername() {
        // Test cases where password contains reversed username
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("nhoj123", "john");
        });
    }

    @Test
    public void testValidPassword() {
        // Test cases with valid passwords
        assertDoesNotThrow(() -> {
            PasswordValidator.validatePassword("SecurePass123", "john");
        });
        
        assertDoesNotThrow(() -> {
            PasswordValidator.validatePassword("MyStrongPassword1", "admin");
        });
    }

    @Test
    public void testPasswordContainsDisplayName() {
        // Test cases where password contains display name
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePasswordAgainstDisplayName("john123", "John Doe");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePasswordAgainstDisplayName("doe123", "John Doe");
        });
    }

    @Test
    public void testValidPasswordAgainstDisplayName() {
        // Test cases with valid passwords against display name
        assertDoesNotThrow(() -> {
            PasswordValidator.validatePasswordAgainstDisplayName("SecurePass123", "John Doe");
        });
    }

    @Test
    public void testCaseInsensitiveValidation() {
        // Test that validation is case insensitive
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("JOHN123", "john");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("john123", "JOHN");
        });
    }

    @Test
    public void testNullValues() {
        // Test handling of null values
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword(null, "john");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordValidator.validatePassword("password", null);
        });
        
        assertDoesNotThrow(() -> {
            PasswordValidator.validatePasswordAgainstDisplayName("password", null);
        });
        
        assertDoesNotThrow(() -> {
            PasswordValidator.validatePasswordAgainstDisplayName(null, "display");
        });
    }
} 