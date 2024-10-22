package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.TokenRepository;
import com.exmpl.authmodule.repositories.UserRepository;
import com.exmpl.authmodule.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    // Create and save a new token for the user,
    // setting the accessToken, refreshToken, user, and expiration time.
    public Token generateTokens(User user, String accessToken, String refreshToken) {
        String hashedAccessToken = hashToken(accessToken);
        String hashedRefreshToken = hashToken(refreshToken);

        Token token = new Token();
        token.setAccessToken(hashedAccessToken);  // Save hashed token
        token.setRefreshToken(hashedRefreshToken); // Save hashed token
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);

    }

    public Optional<Token> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    public boolean validateAccessToken(String accessToken) {
        Optional<Token> tokenOptional = findByAccessToken(accessToken);
        if (tokenOptional.isPresent()) {
            Token token = tokenOptional.get();
            User user = token.getUser(); // Assuming Token has a reference to User
            return JwtUtil.validateToken(accessToken, user); // Validate the token
        }
        return false; // Token not found or invalid
    }
}

