package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.PasswordSetupToken;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.PasswordSetupTokenRepository;
import com.exmpl.authmodule.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordSetupService {

    @Autowired
    private PasswordSetupTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public String createPasswordSetupToken(User user) {
        // Generate a unique token
        String token = UUID.randomUUID().toString();

        // Create token entry
        PasswordSetupToken passwordSetupToken = new PasswordSetupToken();
        passwordSetupToken.setToken(token);
        passwordSetupToken.setUser(user);
        passwordSetupToken.setCreatedAt(LocalDateTime.now());
        passwordSetupToken.setExpiresAt(LocalDateTime.now().plusHours(24)); // Valid for 24 hours
        passwordSetupToken.setUsed(false);

        // Save token
        tokenRepository.save(passwordSetupToken);
        user.setPasswordSetupToken(token);
        userRepository.save(user);
        // Send email
        sendPasswordSetupEmail(user, token);

        return token;
    }

    private void sendPasswordSetupEmail(User user, String token) {
        String passwordSetupLink = "https://your-app.com/password-setup?token=" + token;

        // Prepare placeholders for the email template
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("setupLink", passwordSetupLink);
//        placeholders.put("username", user.getUsername());

        emailService.sendEmail(user.getEmail(), "PASSWORD_SETUP_EMAIL", placeholders);
    }

    public boolean validateToken(String token) {
        Optional<PasswordSetupToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isPresent()) {
            PasswordSetupToken passwordSetupToken = tokenOptional.get();

            // Check if token is expired or already used
            if (passwordSetupToken.getExpiresAt().isBefore(LocalDateTime.now()) || passwordSetupToken.isUsed()) {
                return false;
            }

            return true;
        }

        return false;
    }

    public void markTokenAsUsed(String token) {
        PasswordSetupToken passwordSetupToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        passwordSetupToken.setUsed(true);
        tokenRepository.save(passwordSetupToken);
    }
}
