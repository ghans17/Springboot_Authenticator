package com.exmpl.authmodule.controllers;

import com.exmpl.authmodule.DTOs.LoginRequest;
import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.UserRepository;
import com.exmpl.authmodule.services.TokenService;
import com.exmpl.authmodule.services.UserService;
import com.exmpl.authmodule.utils.JwtUtil;
import com.exmpl.authmodule.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
//        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify password and generate tokens
        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        String accessToken = JwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(user.getUsername());

        Token token = tokenService.generateTokens(user, accessToken, refreshToken);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> tokenRequest) {
        String refreshToken = tokenRequest.get("refreshToken");  // Extract the refresh token from the request body
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing");
        }
        // Decode the refresh token before validating
        String decodedToken = JwtUtil.decodeToken(refreshToken);
        Token storedToken = tokenService.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (JwtUtil.isTokenExpired(storedToken.getRefreshToken())) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken = JwtUtil.generateAccessToken(storedToken.getUser().getUsername());
        storedToken.setAccessToken(newAccessToken);
        tokenService.generateTokens(storedToken.getUser(), newAccessToken, storedToken.getRefreshToken());

        return ResponseEntity.ok(storedToken);
    }
}