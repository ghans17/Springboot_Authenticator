package com.exmpl.authmodule.controllers;

import com.exmpl.authmodule.DTOs.AuthResponse;
import com.exmpl.authmodule.DTOs.LoginRequest;
import com.exmpl.authmodule.entities.OTP;
import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.services.OTPService;
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

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OTPService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Check if OTP validation is required
        Optional<OTP> otpOptional = otpService.findByUsername(loginRequest.getUsername());
        if (otpOptional.isPresent() && otpOptional.get().isValidation()) {
            // If OTP is required, validate it
            boolean otpValid = otpService.validateOTP(loginRequest.getUsername(), loginRequest.getOtpCode());

            if (!otpValid) {
                return ResponseEntity.status(401).body("Invalid OTP");
            }

            // If OTP validation is successful, check if it should be included in the response
            OTP otp = otpOptional.get();
            if (otp.isInResponse()) {
                // Include OTP in the response for debugging
                return ResponseEntity.ok(new AuthResponse(tokenService.getOrCreateToken(user).getAccessTokenHash(), otp.getOtpCode()));
            }
        }
        // Verify password and generate tokens
        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

//        String accessToken = JwtUtil.generateAccessToken(user.getUsername());
//
//
//        // Hash the tokens
//        String hashedAccessToken = tokenService.hashToken(accessToken);

        // Save the normal and hashed access tokens in the database
        Token token =tokenService.getOrCreateToken(user);

        // Return plain tokens in the response
        //return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));

        // Return hashed tokens in the response
        return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));

    }

}