package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.*;
import com.eventhub.eventhub_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Register, login, and password reset")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a USER account and returns a JWT")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        return ResponseEntity.status(201).body(authService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates with email + password, returns a JWT")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/password-reset-request")
    @Operation(summary = "Request password reset",
               description = "Returns a reset token in the response body (use it at /password-reset)")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO dto) {
        return ResponseEntity.ok(authService.requestPasswordReset(dto));
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Reset password", description = "Validates the reset token and sets a new password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody PasswordResetDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}
