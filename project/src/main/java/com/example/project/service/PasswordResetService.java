package com.example.project.service;

import com.example.project.entity.PasswordResetToken;
import com.example.project.entity.User;
import com.example.project.repository.PasswordResetTokenRepository;
import com.example.project.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${app.password-reset.token-expiration:30}")
    private int tokenExpirationMinutes;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;
    
    @Transactional
    public void requestPasswordReset(String email) {
        User user;
        try {
            user = userService.findByEmail(email);
        } catch (RuntimeException e) {
            // Don't reveal if user exists or not for security reasons
            return;
        }
        
        // Ensure user is managed in this transaction
        final User managedUser = entityManager.merge(user);
        
        // Generate new token (ensure it's unique)
        final String token = generateUniqueToken();
        final LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);
        
        // Find existing token for this user and update it, or create a new one
        PasswordResetToken resetToken = tokenRepository.findByUser(managedUser)
                .map(existing -> {
                    // Update existing token (avoids unique constraint violation on user_id)
                    existing.setToken(token);
                    existing.setExpiryDate(expiryDate);
                    existing.setUsed(false);
                    return existing;
                })
                .orElseGet(() -> {
                    // Create new token
                    PasswordResetToken newToken = new PasswordResetToken();
                    newToken.setToken(token);
                    newToken.setUser(managedUser);
                    newToken.setExpiryDate(expiryDate);
                    newToken.setUsed(false);
                    return newToken;
                });
        
        // Save the token (either updated or new)
        tokenRepository.save(resetToken);
        entityManager.flush(); // Ensure it's persisted immediately
        
        // Send email
        sendPasswordResetEmail(managedUser, token);
    }
    
    private String generateUniqueToken() {
        int maxAttempts = 10;
        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            String token = generateSecureToken();
            // Check if token already exists (extremely rare but possible)
            if (tokenRepository.findByToken(token).isEmpty()) {
                return token;
            }
        }
        throw new RuntimeException("Failed to generate unique token. Please try again.");
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Validate password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        
        // Find and validate token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        
        if (resetToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }
        
        if (resetToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }
        
        // Get the user ID from the token
        Long userId = resetToken.getUser().getId();
        
        // Fetch the user fresh from the database to ensure it's managed
        User managedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        // Update the password
        managedUser.setPassword(encodedPassword);
        
        // Save the user - this will trigger @PreUpdate which updates updatedAt
        userRepository.save(managedUser);
        
        // Flush to ensure password is saved to database immediately
        entityManager.flush();
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        entityManager.flush(); // Ensure token is updated
    }
    
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private void sendPasswordResetEmail(User user, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String email = user.getEmail();
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("User email is null or empty");
            }
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            
            // Prepare the evaluation context
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("token", token);
            context.setVariable("resetUrl", appUrl + "/reset-password/confirm?token=" + token);
            context.setVariable("expirationMinutes", tokenExpirationMinutes);
            
            // Create the HTML body using Thymeleaf
            String htmlContent = templateEngine.process("password-reset-email", context);
            if (htmlContent == null) {
                throw new RuntimeException("Failed to generate email template");
            }
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isUsed() && !resetToken.isExpired())
                .orElse(false);
    }
}

