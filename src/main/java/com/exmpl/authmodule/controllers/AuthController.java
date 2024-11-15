package com.exmpl.authmodule.controllers;

import com.exmpl.authmodule.DTOs.AuthResponse;
import com.exmpl.authmodule.DTOs.LoginRequest;
import com.exmpl.authmodule.DTOs.OtpValidationRequest;
import com.exmpl.authmodule.DTOs.PasswordSetupRequest;
import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.OtpRepository;
import com.exmpl.authmodule.services.OTPService;
import com.exmpl.authmodule.services.PasswordSetupService;
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

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordSetupService passwordSetupService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Save the new user
        User savedUser = userService.registerUser(user);

        // Create a password setup token and send email
        passwordSetupService.createPasswordSetupToken(savedUser);

        return ResponseEntity.ok("Registration successful. Password setup link has been sent to your email.");
    }

    @PostMapping("/password-setup/complete")
    public ResponseEntity<?> completePasswordSetup(@RequestBody PasswordSetupRequest request) {
        if (!passwordSetupService.validateToken(request.getToken())) {
            return ResponseEntity.status(400).body("Invalid or expired token");
        }

        User user = userService.findByPasswordSetupToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(PasswordUtil.hashPassword(request.getNewPassword()));
        userService.saveUser(user);

        passwordSetupService.markTokenAsUsed(request.getToken());
        return ResponseEntity.ok("Password has been successfully updated.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure the user has set their password
        if (user.getPassword() == null) {
            return ResponseEntity.status(400).body("Password not set. Please set your password using the setup link sent to your email.");
        }

        // Verify password
        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        // Check if OTP is required for this user
        if (otpService.isOtpRequiredForLogin()) {
            // Generate and send OTP if needed
            String otp = otpService.generateOtpForUser(user);

            // Check if OTP should be included in response (for debugging)
            if (otpService.isOtpInResponse()) {
                return ResponseEntity.ok("OTP: " + otp);
            }

            return ResponseEntity.ok("OTP has been sent to your email.");
        }

        // No OTP required, proceed with generating the access token
        Token token = tokenService.getOrCreateToken(user);
        return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));
    }


    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        User user = userService.findByUsername(otpValidationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the OTP
        boolean isValid = otpService.validateOtp(user, otpValidationRequest.getOtpCode());
        if (!isValid) {
            return ResponseEntity.status(400).body("Invalid OTP");
        }

        // OTP is valid, proceed to generate token or other actions
        Token token = tokenService.getOrCreateToken(user);
        return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));
    }
}

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        User user = userService.findByUsername(loginRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//
//        // Check if OTP validation is required
//        Optional<OTP> otpOptional = otpService.findByUser(loginRequest.getUsername());
//        if (otpOptional.isPresent() && otpOptional.get().isValidation()) {
//            // If OTP is required, validate it
//            boolean otpValid = otpService.validateOTP(loginRequest.getUsername(), loginRequest.getOtpCode());
//
//            if (!otpValid) {
//                return ResponseEntity.status(401).body("Invalid OTP");
//            }
//
//            // If OTP validation is successful, check if it should be included in the response
//            OTP otp = otpOptional.get();
//            if (otp.isInResponse()) {
//                // Include OTP in the response for debugging
//                return ResponseEntity.ok(new AuthResponse(tokenService.getOrCreateToken(user).getAccessTokenHash(), otp.getOtpCode()));
//            }
//        }
//        // Verify password and generate tokens
//        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(401).body("Invalid password");
//        }

//        String accessToken = JwtUtil.generateAccessToken(user.getUsername());
//
//
//        // Hash the tokens
//        String hashedAccessToken = tokenService.hashToken(accessToken);

        // Save the normal and hashed access tokens in the database
//        Token token =tokenService.getOrCreateToken(user);
//
//        // Return plain tokens in the response
//        //return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
//
//        // Return hashed tokens in the response
//        return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));
//
//    }

