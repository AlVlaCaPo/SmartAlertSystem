package com.smartalert.auth.service;

import com.smartalert.auth.dto.ApiResponse;
import com.smartalert.auth.dto.AuthResponse;
import com.smartalert.auth.dto.LoginRequest;
import com.smartalert.auth.dto.RegisterRequest;
import com.smartalert.auth.exception.AuthException;
import com.smartalert.auth.model.User;
import com.smartalert.auth.repository.UserRepository;
import com.smartalert.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public ApiResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Error: Email is already in use!");
        }

        Set<String> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("ROLE_USER");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(roles)
                .build();

        userRepository.save(user);

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User registered successfully!")
                .details("Registration completed for user: " + user.getUsername())
                .status(200)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("Error: User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Error: Invalid password!");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRoles());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }
}
