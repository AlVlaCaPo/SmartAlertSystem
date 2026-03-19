package com.smartalert.auth.service;

import com.smartalert.auth.dto.ApiResponse;
import com.smartalert.auth.dto.AuthResponse;
import com.smartalert.auth.dto.LoginRequest;
import com.smartalert.auth.dto.RegisterRequest;
import com.smartalert.auth.exception.AuthException;
import com.smartalert.auth.model.User;
import com.smartalert.auth.repository.UserRepository;
import com.smartalert.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roles(Set.of("ROLE_USER"))
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .roles(Set.of("ROLE_USER"))
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ApiResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("User registered successfully!", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameTaken_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> authService.register(registerRequest));
        assertTrue(exception.getMessage().contains("Username is already taken"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailTaken_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> authService.register(registerRequest));
        assertTrue(exception.getMessage().contains("Email is already in use"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString(), anySet())).thenReturn("mockToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("Invalid password"));
    }
}
