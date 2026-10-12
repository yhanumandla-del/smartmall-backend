package com.smartmall.service;

import com.smartmall.entity.PasswordResetToken;
import com.smartmall.entity.User;
import com.smartmall.repository.PasswordResetTokenRepository;
import com.smartmall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class PasswordResetService {

    private static final String INVALID_TOKEN = "This reset link is invalid or has expired. Please request a new one.";

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final MailService mail;
    private final long expiryMinutes;
    private final String frontendUrl;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(
        UserRepository users,
        PasswordResetTokenRepository tokens,
        PasswordEncoder encoder,
        MailService mail,
        @Value("${app.reset.expiry-minutes:30}") long expiryMinutes,
        @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.mail = mail;
        this.expiryMinutes = expiryMinutes;
        this.frontendUrl = frontendUrl.trim().replaceAll("/+$", "");
    }

    /**
     * Creates a reset token and emails the link. Does nothing (silently) if the email is unknown,
     * so the endpoint cannot be used to find out which emails are registered.
     */
    @Transactional
    public void requestReset(String rawEmail) {
        if (rawEmail == null || rawEmail.isBlank()) {
            return;
        }

        User user = users.findByEmail(rawEmail.trim().toLowerCase()).orElse(null);
        if (user == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetToken> existing = tokens.findByUser(user);

        // Simple throttle: ignore repeat requests within 60 seconds.
        boolean requestedJustNow = existing.stream()
            .anyMatch(t -> t.getCreatedAt().isAfter(now.minusSeconds(60)));
        if (requestedJustNow) {
            return;
        }

        tokens.deleteAll(existing);

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        tokens.save(new PasswordResetToken(sha256(token), user, now.plusMinutes(expiryMinutes)));

        String link = frontendUrl + "/#/reset-password?token=" + token;
        mail.sendPasswordReset(user.getEmail(), user.getName(), link, expiryMinutes);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(INVALID_TOKEN);
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 100) {
            throw new IllegalArgumentException("Password must be between 6 and 100 characters.");
        }

        PasswordResetToken saved = tokens.findByTokenHash(sha256(token.trim()))
            .orElseThrow(() -> new IllegalArgumentException(INVALID_TOKEN));

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(INVALID_TOKEN);
        }

        User user = saved.getUser();
        user.setPassword(encoder.encode(newPassword));
        users.save(user);

        // Single use: remove every outstanding token for this user.
        tokens.deleteAll(tokens.findByUser(user));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
