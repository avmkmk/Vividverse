package com.vividverse.user.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    
    /**
     * Validates password against security requirements
     * @param password the password to validate
     * @param username the username to check against
     * @throws IllegalArgumentException if password doesn't meet security requirements
     */
    public static void validatePassword(String password, String username) {
        if (password == null || username == null) {
            throw new IllegalArgumentException("Password and username cannot be null");
        }
        
        // Check if password contains username or parts of username
        if (containsUsername(password, username)) {
            throw new IllegalArgumentException("Password cannot contain your username or parts of your username");
        }
        
        // Check if password contains display name (if provided)
        // This would be called separately if display name is available
        
        // Additional security checks can be added here
        validatePasswordStrength(password);
    }
    
    /**
     * Checks if password contains username or parts of username
     * @param password the password to check
     * @param username the username to check against
     * @return true if password contains username or parts of username
     */
    private static boolean containsUsername(String password, String username) {
        String lowerPassword = password.toLowerCase();
        String lowerUsername = username.toLowerCase();
        
        // Check if password contains the full username
        if (lowerPassword.contains(lowerUsername)) {
            return true;
        }
        
        // Check if password contains parts of username (substrings of 3 or more characters)
        for (int i = 0; i <= lowerUsername.length() - 3; i++) {
            for (int j = i + 3; j <= lowerUsername.length(); j++) {
                String usernamePart = lowerUsername.substring(i, j);
                if (lowerPassword.contains(usernamePart)) {
                    return true;
                }
            }
        }
        
        // Check for reversed username
        String reversedUsername = new StringBuilder(lowerUsername).reverse().toString();
        if (lowerPassword.contains(reversedUsername)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Validates password strength
     * @param password the password to validate
     * @throws IllegalArgumentException if password doesn't meet strength requirements
     */
    private static void validatePasswordStrength(String password) {
        // Check minimum length (already handled by @Size annotation, but double-check)
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        // Check for common weak patterns
        if (isCommonWeakPassword(password)) {
            throw new IllegalArgumentException("Password is too common or weak. Please choose a stronger password");
        }
        
        // Optional: Add more strength requirements
        // For example, require at least one uppercase, one lowercase, one digit
        if (!Pattern.compile(".*[A-Z].*").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        
        if (!Pattern.compile(".*[a-z].*").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        
        if (!Pattern.compile(".*\\d.*").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
    }
    
    /**
     * Checks if password is a common weak password
     * @param password the password to check
     * @return true if password is common/weak
     */
    private static boolean isCommonWeakPassword(String password) {
        String lowerPassword = password.toLowerCase();
        
        // List of common weak passwords
        String[] commonPasswords = {
            "password", "123456", "123456789", "qwerty", "abc123", 
            "password123", "admin", "letmein", "welcome", "monkey",
            "dragon", "master", "hello", "freedom", "whatever",
            "qwerty123", "trustno1", "jordan", "harley", "ranger",
            "iwantu", "jennifer", "joshua", "maggie", "password1",
            "robert", "daniel", "heather", "michelle", "charlie"
        };
        
        for (String common : commonPasswords) {
            if (lowerPassword.equals(common)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Validates password against display name (if available)
     * @param password the password to validate
     * @param displayName the display name to check against
     * @throws IllegalArgumentException if password contains display name
     */
    public static void validatePasswordAgainstDisplayName(String password, String displayName) {
        if (password == null || displayName == null) {
            return; // Skip validation if either is null
        }
        
        String lowerPassword = password.toLowerCase();
        String lowerDisplayName = displayName.toLowerCase();
        
        // Check if password contains display name or parts of display name
        if (containsDisplayName(lowerPassword, lowerDisplayName)) {
            throw new IllegalArgumentException("Password cannot contain your display name or parts of your display name");
        }
    }
    
    /**
     * Checks if password contains display name or parts of display name
     * @param lowerPassword the lowercase password
     * @param lowerDisplayName the lowercase display name
     * @return true if password contains display name or parts of display name
     */
    private static boolean containsDisplayName(String lowerPassword, String lowerDisplayName) {
        // Check if password contains the full display name
        if (lowerPassword.contains(lowerDisplayName)) {
            return true;
        }
        
        // Check if password contains parts of display name (substrings of 3 or more characters)
        for (int i = 0; i <= lowerDisplayName.length() - 3; i++) {
            for (int j = i + 3; j <= lowerDisplayName.length(); j++) {
                String displayNamePart = lowerDisplayName.substring(i, j);
                if (lowerPassword.contains(displayNamePart)) {
                    return true;
                }
            }
        }
        
        return false;
    }
} 