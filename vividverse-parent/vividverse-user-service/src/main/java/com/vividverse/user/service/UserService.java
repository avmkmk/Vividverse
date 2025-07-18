package com.vividverse.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // For password hashing
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transaction management

import com.vividverse.user.dto.UserLoginRequest;
import com.vividverse.user.dto.UserProfileResponse;
import com.vividverse.user.dto.UserRegisterRequest;
import com.vividverse.user.entity.User;
import com.vividverse.user.repository.UserRepository;
import com.vividverse.user.util.PasswordValidator; // Import the new password validator

@Service // Marks this class as a Spring Service component
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Used for secure password hashing

    // Constructor injection of UserRepository and BCryptPasswordEncoder
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Initialize password encoder
    }

    @Transactional // Ensures the entire method runs within a single database transaction
    public UserProfileResponse registerUser(UserRegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // Validate password against security requirements
        try {
            PasswordValidator.validatePassword(request.getPassword(), request.getUsername());
            // Also validate against display name if it's different from username
            if (!request.getDisplayName().equalsIgnoreCase(request.getUsername())) {
                PasswordValidator.validatePasswordAgainstDisplayName(request.getPassword(), request.getDisplayName());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Password validation failed: " + e.getMessage());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Hash the password
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName());

        User savedUser = userRepository.save(user); // Save the new user to the database

        return convertToDto(savedUser); // Convert entity to DTO for response
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public Optional<UserProfileResponse> loginUser(UserLoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Compare raw password with hashed password
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                return Optional.of(convertToDto(user)); // Login successful, return user profile
            }
        }
        return Optional.empty(); // Login failed
    }

    @Transactional(readOnly = true)
    public Optional<UserProfileResponse> getUserProfile(UUID id) {
        return userRepository.findById(id)
                             .map(this::convertToDto); // Find by ID and convert to DTO
    }

    // Helper method to convert User entity to UserProfileResponse DTO
    private UserProfileResponse convertToDto(User user) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}