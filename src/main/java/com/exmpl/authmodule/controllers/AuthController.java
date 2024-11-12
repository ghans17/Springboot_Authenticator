package com.exmpl.authmodule.controllers;

import com.exmpl.authmodule.DTOs.AuthResponse;
import com.exmpl.authmodule.DTOs.LoginRequest;
import com.exmpl.authmodule.entities.User;
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



@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register/1.0.0")
    public ResponseEntity<?> register(@RequestBody User user) {
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


        // Hash the tokens
        String hashedAccessToken = tokenService.hashToken(accessToken);

        // Save the normal and hashed access tokens in the database
        tokenService.generateTokens(user, accessToken, hashedAccessToken);

        // Return plain tokens in the response
        //return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));

        // Return hashed tokens in the response
        return ResponseEntity.ok(new AuthResponse(hashedAccessToken));

    }

}