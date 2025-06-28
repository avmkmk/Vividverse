package com.vividverse.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vividverse.user.dto.UserLoginRequest;
import com.vividverse.user.dto.UserProfileResponse;
import com.vividverse.user.dto.UserRegisterRequest;
import com.vividverse.user.entity.User;
import com.vividverse.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRegisterRequest validRegisterRequest;
    private UserLoginRequest validLoginRequest;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("Test User");
        testUser.setPasswordHash(new BCryptPasswordEncoder().encode("SecurePass123"));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        validRegisterRequest = new UserRegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setPassword("SecurePass123");
        validRegisterRequest.setEmail("newuser@example.com");
        validRegisterRequest.setDisplayName("New User");

        validLoginRequest = new UserLoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("SecurePass123");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserProfileResponse result = userService.registerUser(validRegisterRequest);

        // Assert
        assertNotNull(result);
        assertEquals(validRegisterRequest.getUsername(), result.getUsername());
        assertEquals(validRegisterRequest.getEmail(), result.getEmail());
        assertEquals(validRegisterRequest.getDisplayName(), result.getDisplayName());
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository).existsByEmail(validRegisterRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(validRegisterRequest);
        });
        assertEquals("Username already taken.", exception.getMessage());
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(validRegisterRequest);
        });
        assertEquals("Email already registered.", exception.getMessage());
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository).existsByEmail(validRegisterRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_PasswordContainsUsername() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("john");
        request.setPassword("john123");
        request.setEmail("john@example.com");
        request.setDisplayName("John Doe");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });
        assertTrue(exception.getMessage().contains("Password validation failed"));
        assertTrue(exception.getMessage().contains("Password cannot contain your username"));
    }

    @Test
    void testRegisterUser_PasswordContainsDisplayName() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user123");
        request.setPassword("doe123");
        request.setEmail("user@example.com");
        request.setDisplayName("John Doe");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });
        assertTrue(exception.getMessage().contains("Password validation failed"));
        assertTrue(exception.getMessage().contains("Password cannot contain your display name"));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        when(userRepository.findByUsername(validLoginRequest.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserProfileResponse> result = userService.loginUser(validLoginRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findByUsername(validLoginRequest.getUsername());
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(validLoginRequest.getUsername())).thenReturn(Optional.empty());

        // Act
        Optional<UserProfileResponse> result = userService.loginUser(validLoginRequest);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername(validLoginRequest.getUsername());
    }

    @Test
    void testLoginUser_WrongPassword() {
        // Arrange
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest();
        wrongPasswordRequest.setUsername("testuser");
        wrongPasswordRequest.setPassword("WrongPassword123");

        when(userRepository.findByUsername(wrongPasswordRequest.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserProfileResponse> result = userService.loginUser(wrongPasswordRequest);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername(wrongPasswordRequest.getUsername());
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserProfileResponse> result = userService.getUserProfile(testUserId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        assertEquals(testUser.getDisplayName(), result.get().getDisplayName());
        verify(userRepository).findById(testUserId);
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfileResponse> result = userService.getUserProfile(nonExistentUserId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(nonExistentUserId);
    }

    @Test
    void testRegisterUser_WeakPassword() {
        // Arrange
        UserRegisterRequest weakPasswordRequest = new UserRegisterRequest();
        weakPasswordRequest.setUsername("newuser");
        weakPasswordRequest.setPassword("password"); // Common weak password
        weakPasswordRequest.setEmail("newuser@example.com");
        weakPasswordRequest.setDisplayName("New User");

        when(userRepository.existsByUsername(weakPasswordRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(weakPasswordRequest.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(weakPasswordRequest);
        });
        assertTrue(exception.getMessage().contains("Password validation failed"));
        assertTrue(exception.getMessage().contains("too common or weak"));
    }

    @Test
    void testRegisterUser_PasswordTooShort() {
        // Arrange
        UserRegisterRequest shortPasswordRequest = new UserRegisterRequest();
        shortPasswordRequest.setUsername("newuser");
        shortPasswordRequest.setPassword("abc"); // Too short
        shortPasswordRequest.setEmail("newuser@example.com");
        shortPasswordRequest.setDisplayName("New User");

        when(userRepository.existsByUsername(shortPasswordRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(shortPasswordRequest.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(shortPasswordRequest);
        });
        assertTrue(exception.getMessage().contains("Password validation failed"));
        assertTrue(exception.getMessage().contains("at least 8 characters"));
    }

    @Test
    void testRegisterUser_PasswordMissingUppercase() {
        // Arrange
        UserRegisterRequest noUppercaseRequest = new UserRegisterRequest();
        noUppercaseRequest.setUsername("newuser");
        noUppercaseRequest.setPassword("password123"); // No uppercase
        noUppercaseRequest.setEmail("newuser@example.com");
        noUppercaseRequest.setDisplayName("New User");

        when(userRepository.existsByUsername(noUppercaseRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(noUppercaseRequest.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(noUppercaseRequest);
        });
        assertTrue(exception.getMessage().contains("Password validation failed"));
        assertTrue(exception.getMessage().contains("uppercase letter"));
    }
} 