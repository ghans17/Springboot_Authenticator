package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.TokenRepository;
import com.exmpl.authmodule.repositories.UserRepository;
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

    // Create and save a new token for the user,
    // setting the hashed accessToken, hashed refreshToken, user, and expiration time.
    public Token generateTokens(User user, String accessTokenHash, String refreshTokenHash) {
        Token token = new Token();
        token.setAccessTokenHash(accessTokenHash); // Store hashed access token
        token.setRefreshTokenHash(refreshTokenHash); // Store hashed refresh token
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);
    }

    public Optional<Token> findByAccessTokenHash(String accessTokenHash) {
        return tokenRepository.findByAccessTokenHash(accessTokenHash);
    }

    public Optional<Token> findByRefreshTokenHash(String refreshTokenHash) {
        return tokenRepository.findByRefreshTokenHash(refreshTokenHash);
    }

    public boolean validateAccessToken(String accessToken) {
       //String hashedToken = hashToken(accessToken); // Hash the incoming token
        Optional<Token> tokenOptional = findByAccessTokenHash(accessToken); // Find by hashed token
        return tokenOptional.isPresent(); // Return true if token exists
        //return tokenOptional.isPresent() && !JwtUtil.isTokenExpired(accessToken);
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
