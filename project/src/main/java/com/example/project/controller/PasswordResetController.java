package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.PasswordResetConfirmRequest;
import com.example.project.dto.PasswordResetRequest;
import com.example.project.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/reset-password")
@RequiredArgsConstructor
public class PasswordResetController {
    
    private final PasswordResetService passwordResetService;
    
    @PostMapping("/request")
    public ResponseEntity<ApiResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            // Always return success message for security (don't reveal if email exists)
            return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred while processing your request"));
        }
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password has been reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Optional convenience: if someone GETs the API URL, redirect to the UI form
    @GetMapping("/confirm")
    public ResponseEntity<Void> redirectConfirmToUi(@RequestParam String token) {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", "/reset-password/confirm?token=" + token)
            .build();
    }
    
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("Token is valid"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Token is invalid or expired"));
        }
    }
}

