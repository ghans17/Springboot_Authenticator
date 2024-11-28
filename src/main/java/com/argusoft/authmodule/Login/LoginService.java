package com.argusoft.authmodule.Login;

import com.argusoft.authmodule.entities.Token;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.services.OtpService;
import com.argusoft.authmodule.services.TokenService;
import com.argusoft.authmodule.services.UserService;
import com.argusoft.authmodule.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private OtpService otpService;

    @Autowired
    private TokenService tokenService;

    public Object login(LoginRequest loginRequest) throws Exception {
        // Find the user by username
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the user has set their password
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Password not set. Please set your password using the setup link sent to your email.");
        }

        // Verify password
        if (!passwordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password");
        }

        // Check if OTP is required
        if (otpService.isOtpRequiredForLogin()) {
            String otp = otpService.generateOtpForUser(user);

            // Check if OTP should be included in the response for debugging purposes
            if (otpService.isOtpInResponse()) {
                return Map.of("message", "OTP generated", "otp", otp);
            }

            return Map.of("message", "OTP has been sent to your email.");
        }

        // No OTP required, generate the access token
        Token token = tokenService.getOrCreateToken(user);

        // Return the hashed access token
        return new LoginResponse(token.getAccessTokenHash());
    }
}

