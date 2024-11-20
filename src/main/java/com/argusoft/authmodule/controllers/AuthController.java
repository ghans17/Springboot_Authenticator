package com.argusoft.authmodule.controllers;

import com.argusoft.authmodule.DTOs.AuthResponse;
import com.argusoft.authmodule.DTOs.LoginRequest;
import com.argusoft.authmodule.DTOs.OtpValidationRequest;
import com.argusoft.authmodule.DTOs.PasswordSetupRequest;
import com.argusoft.authmodule.entities.PasswordSetupToken;
import com.argusoft.authmodule.entities.Token;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.OtpRepository;
import com.argusoft.authmodule.services.OTPService;
import com.argusoft.authmodule.services.PasswordSetupService;
import com.argusoft.authmodule.services.TokenService;
import com.argusoft.authmodule.services.UserService;
import com.argusoft.authmodule.utils.PasswordUtil;
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

    @PostMapping("/password-setup")
    public ResponseEntity<?> completePasswordSetup(@RequestBody PasswordSetupRequest request) {

        PasswordSetupToken setupToken = passwordSetupService.validateToken(request.getToken());
        if (setupToken == null) {
            return ResponseEntity.status(400).body("Invalid or expired token");
        }

        // Retrieve the user associated with the token
        User user = setupToken.getUser();
        if (user == null) {
            return ResponseEntity.status(404).body("User not found for this token");
        }


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
        }else {

            // No OTP required,then proceed with generating the access token
            Token token = tokenService.getOrCreateToken(user);
            return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));
        }
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

        // OTP is valid,then proceed to generate token
        Token token = tokenService.getOrCreateToken(user);
        return ResponseEntity.ok(new AuthResponse(token.getAccessTokenHash()));
    }
}
