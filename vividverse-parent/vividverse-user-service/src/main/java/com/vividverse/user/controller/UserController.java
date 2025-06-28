package com.vividverse.user.controller;

import com.vividverse.user.dto.UserLoginRequest;
import com.vividverse.user.dto.UserProfileResponse;
import com.vividverse.user.dto.UserRegisterRequest;
import com.vividverse.user.service.UserService;
import jakarta.validation.Valid; // For validation annotations
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional; // Import Optional
import java.util.UUID;

@RestController // Marks this class as a REST controller
@RequestMapping("/users") // Base path for all endpoints in this controller
public class UserController {

    private final UserService userService;

    // Constructor injection of UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        try {
            UserProfileResponse newUser = userService.registerUser(request);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED); // 201 Created 
        } catch (IllegalArgumentException e) {
            // Return a String message with BAD_REQUEST status
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest request) {
        // In a real app, this would return a JWT or session token.
        // For MVP, we just return the user profile on successful login.
        Optional<UserProfileResponse> userProfileOptional = userService.loginUser(request);
        if (userProfileOptional.isPresent()) {
            // If user is found and password matches, return 200 OK with UserProfileResponse
            return ResponseEntity.ok(userProfileOptional.get());
        } else {
            // If login fails (user not found or password mismatch), return 401 Unauthorized with String body
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable UUID userId) {
        Optional<UserProfileResponse> userProfileOptional = userService.getUserProfile(userId);
        if (userProfileOptional.isPresent()) {
            // If user is found, return 200 OK with UserProfileResponse
            return ResponseEntity.ok(userProfileOptional.get());
        } else {
            // If user is not found, return 404 Not Found with String body
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
}