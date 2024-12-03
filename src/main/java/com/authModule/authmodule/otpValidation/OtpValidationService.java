package com.authModule.authmodule.otpValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.Login.LoginResponse;
import com.authModule.authmodule.entities.Token;
import com.authModule.authmodule.entities.User;
import com.authModule.authmodule.services.OtpService;
import com.authModule.authmodule.services.TokenService;
import com.authModule.authmodule.services.UserService;

@Service
public class OtpValidationService {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private TokenService tokenService;

    public LoginResponse validateOtp(OtpValidationRequest otpValidationRequest) {
        // Find the user by username
        User user = userService.findByUsername(otpValidationRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate the OTP
        boolean isValid = otpService.validateOtp(user, otpValidationRequest.getOtpCode());
        if (!isValid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // OTP is valid, proceed to generate the token
        Token token = tokenService.getOrCreateToken(user);

        // Return the response with the hashed access token
        return new LoginResponse(token.getAccessTokenHash());
    }
}

