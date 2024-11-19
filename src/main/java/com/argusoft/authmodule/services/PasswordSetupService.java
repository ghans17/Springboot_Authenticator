package com.argusoft.authmodule.services;

import com.argusoft.authmodule.entities.PasswordSetupToken;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.PasswordSetupTokenRepository;
import com.argusoft.authmodule.repositories.UserRepository;
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

        // Send email
        sendPasswordSetupEmail(user, token);

        return token;
    }

    private void sendPasswordSetupEmail(User user, String token) {
        String passwordSetupLink = "https://app.com/password-setup?token=" + token;

        // Prepare placeholders for the email template
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("setupLink", passwordSetupLink);
//        placeholders.put("username", user.getUsername());

        emailService.sendEmail(user.getEmail(), "PASSWORD_SETUP_EMAIL", placeholders);
    }

    public PasswordSetupToken validateToken(String token) {
        Optional<PasswordSetupToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isPresent()) {
            PasswordSetupToken setupToken = tokenOptional.get();
            // Check if the token is expired or has already been used
            if (setupToken.getExpiresAt().isBefore(LocalDateTime.now()) || setupToken.isUsed()) {
                return null;  // Token has expired or is already used
            }
            return setupToken;  // Token is valid
        }
        return null;  // Token does not exist
    }


    //Mark the token as used after setting the password
    public void markTokenAsUsed(String token) {
        PasswordSetupToken passwordSetupToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        passwordSetupToken.setUsed(true);
        tokenRepository.save(passwordSetupToken);
    }

}

