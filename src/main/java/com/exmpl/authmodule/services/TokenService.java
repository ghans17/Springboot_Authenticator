package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.TokenRepository;
import com.exmpl.authmodule.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;


@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;
    // Method to check for existing token or create a new one
    public Token getOrCreateToken(User user) {
        Optional<Token> existingTokenOptional = tokenRepository.findByUser(user);

        if (existingTokenOptional.isPresent()) {
            Token existingToken = existingTokenOptional.get();
            // If the existing token is valid, extend the expiration time
            if (existingToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                existingToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Extend expiry
                return tokenRepository.save(existingToken);
            }
        }

        // If no valid token exists, create a new one
        String accessToken = JwtUtil.generateAccessToken(user.getUsername()); // Generate new token
        String hashedAccessToken = hashToken(accessToken); // Hash the token before saving

        Token newToken = new Token();
        newToken.setUser(user);
        newToken.setAccessToken(accessToken);
        newToken.setAccessTokenHash(hashedAccessToken);
        newToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Set expiration time
        return tokenRepository.save(newToken); // Save the new token
    }

    public void save(Token token) {
        tokenRepository.save(token);
    }


    //check if the incoming hashed token matches any stored token
    public Optional<Token> findByAccessTokenHash(String accessTokenHash) {
        return tokenRepository.findByAccessTokenHash(accessTokenHash);
    }

    public Optional<Token> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    public Optional<User> getUserFromToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByAccessToken(token);
        return tokenOptional.map(Token::getUser); // Returns the user if token is valid
    }


    public boolean validateAccessToken(String accessToken) {

        Optional<Token> tokenOptional = findByAccessTokenHash(accessToken); // Find by hashed token
        return tokenOptional.isPresent() && tokenOptional.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    // Hashing method
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes); // Convert the hash to Base64 string for storage
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }



}
