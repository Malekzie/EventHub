package com.eventhub.eventhub_api.service;

import com.eventhub.eventhub_api.dto.*;
import com.eventhub.eventhub_api.model.Role;
import com.eventhub.eventhub_api.model.User;
import com.eventhub.eventhub_api.repository.UserRepository;
import com.eventhub.eventhub_api.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponseDTO register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new LoginResponseDTO(
                jwtUtil.generateToken(user.getEmail()),
                user.getEmail(),
                user.getRole().name());
    }

    public LoginResponseDTO login(LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return new LoginResponseDTO(
                jwtUtil.generateToken(user.getEmail()),
                user.getEmail(),
                user.getRole().name());
    }

    @Transactional
    public Map<String, String> requestPasswordReset(PasswordResetRequestDTO dto) {
        return userRepository.findByEmail(dto.getEmail())
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    user.setResetToken(token);
                    user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
                    userRepository.save(user);
                    return Map.of(
                            "resetToken", token,
                            "message", "Use this token at /api/v1/auth/password-reset to set a new password");
                })
                .orElseGet(() -> Map.of(
                        "message", "If that email is registered, a reset token has been sent"));
    }

    @Transactional
    public Map<String, String> resetPassword(PasswordResetDTO dto) {
        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return Map.of("message", "Password reset successfully. You can now log in with your new password.");
    }
}
