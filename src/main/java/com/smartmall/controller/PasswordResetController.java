package com.smartmall.controller;

import com.smartmall.dto.AuthDtos.ForgotPasswordRequest;
import com.smartmall.dto.AuthDtos.ResetPasswordRequest;
import com.smartmall.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        service.requestReset(request.email());
        // Same answer whether or not the email exists, to avoid revealing registered accounts.
        return ResponseEntity.ok(Map.of(
            "message", "If an account exists for that email, a password reset link has been sent."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        service.resetPassword(request.token(), request.password());
        return ResponseEntity.ok(Map.of(
            "message", "Your password has been updated. You can now sign in."
        ));
    }
}
