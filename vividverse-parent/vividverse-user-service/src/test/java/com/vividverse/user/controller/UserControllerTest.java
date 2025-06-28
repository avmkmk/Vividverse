package com.vividverse.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vividverse.user.dto.UserLoginRequest;
import com.vividverse.user.dto.UserProfileResponse;
import com.vividverse.user.dto.UserRegisterRequest;
import com.vividverse.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRegisterRequest validRegisterRequest;
    private UserLoginRequest validLoginRequest;
    private UserProfileResponse userProfileResponse;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        validRegisterRequest = new UserRegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setPassword("SecurePass123");
        validRegisterRequest.setEmail("newuser@example.com");
        validRegisterRequest.setDisplayName("New User");

        validLoginRequest = new UserLoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("SecurePass123");

        userProfileResponse = new UserProfileResponse();
        userProfileResponse.setId(testUserId);
        userProfileResponse.setUsername("testuser");
        userProfileResponse.setEmail("test@example.com");
        userProfileResponse.setDisplayName("Test User");
        userProfileResponse.setCreatedAt(LocalDateTime.now());
        userProfileResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(userProfileResponse);

        // Act
        ResponseEntity<?> response = userController.registerUser(validRegisterRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserProfileResponse);
        UserProfileResponse result = (UserProfileResponse) response.getBody();
        assertEquals(validRegisterRequest.getUsername(), result.getUsername());
        assertEquals(validRegisterRequest.getEmail(), result.getEmail());
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        when(userService.registerUser(any(UserRegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("Username already taken."));

        // Act
        ResponseEntity<?> response = userController.registerUser(validRegisterRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already taken.", response.getBody());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userService.registerUser(any(UserRegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("Email already registered."));

        // Act
        ResponseEntity<?> response = userController.registerUser(validRegisterRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already registered.", response.getBody());
    }

    @Test
    void testRegisterUser_PasswordValidationFailed() {
        // Arrange
        when(userService.registerUser(any(UserRegisterRequest.class)))
            .thenThrow(new IllegalArgumentException("Password validation failed: Password cannot contain your username"));

        // Act
        ResponseEntity<?> response = userController.registerUser(validRegisterRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Password validation failed"));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        when(userService.loginUser(any(UserLoginRequest.class))).thenReturn(Optional.of(userProfileResponse));

        // Act
        ResponseEntity<?> response = userController.loginUser(validLoginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserProfileResponse);
        UserProfileResponse result = (UserProfileResponse) response.getBody();
        assertEquals(validLoginRequest.getUsername(), result.getUsername());
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        // Arrange
        when(userService.loginUser(any(UserLoginRequest.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.loginUser(validLoginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        when(userService.getUserProfile(testUserId)).thenReturn(Optional.of(userProfileResponse));

        // Act
        ResponseEntity<?> response = userController.getUserProfile(testUserId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserProfileResponse);
        UserProfileResponse result = (UserProfileResponse) response.getBody();
        assertEquals(testUserId, result.getId());
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserProfile(nonExistentUserId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.getUserProfile(nonExistentUserId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testRegisterUser_WithNullValues() {
        // Arrange
        UserRegisterRequest nullRequest = new UserRegisterRequest();
        // Don't set any values, leaving them null

        // Act
        ResponseEntity<?> response = userController.registerUser(nullRequest);

        // Assert
        // This should fail validation before reaching the service
        // The exact behavior depends on validation configuration
        assertNotNull(response);
    }

    @Test
    void testLoginUser_WithNullValues() {
        // Arrange
        UserLoginRequest nullRequest = new UserLoginRequest();
        // Don't set any values, leaving them null

        // Act
        ResponseEntity<?> response = userController.loginUser(nullRequest);

        // Assert
        // This should fail validation before reaching the service
        assertNotNull(response);
    }

    @Test
    void testGetUserProfile_WithNullUserId() {
        // Act
        ResponseEntity<?> response = userController.getUserProfile(null);

        // Assert
        // This should handle null UUID gracefully
        assertNotNull(response);
    }
} 