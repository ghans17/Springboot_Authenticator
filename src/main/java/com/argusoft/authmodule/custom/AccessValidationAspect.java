package com.argusoft.authmodule.custom;

import com.argusoft.authmodule.entities.Token;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.AppIdRepository;
import com.argusoft.authmodule.services.TokenService;
import com.argusoft.authmodule.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
@Aspect
@Component
@Order(1)
public class AccessValidationAspect {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

        @Before("@annotation(validateAccess)")
        public void validateToken(JoinPoint joinPoint, ValidateAccess validateAccess) throws Throwable {
            String token = getTokenFromRequest();

            // Validate the token
            if (token == null || !tokenService.validateAccessToken(token)) {
                throw new RuntimeException(validateAccess.message());
            }

            // If valid, extend the expiration time
            Optional<Token> optionalExistingToken = tokenService.findByAccessTokenHash(token);
            if (optionalExistingToken.isPresent()) {
                Token existingToken = optionalExistingToken.get(); // Extract the Token from Optional
                existingToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Extend expiry
                tokenService.save(existingToken); // Save updated token
            }

            String username = extractUsername(token);

            // Validate App-ID access
            String appId = validateAccess.appId(); // Get the App-ID from the annotation
            if (!hasAccessToAppId(username, appId)) {
                throw new RuntimeException("User does not have access to this App-ID.");
            }
        }


    // Extract username from the token
        private String extractUsername(String token) {
            // Find the token by its hash and extract the associated user
            Optional<Token> tokenOptional = tokenService.findByAccessTokenHash(token);
            if (tokenOptional.isPresent()) {
                Token tokenEntity = tokenOptional.get();
                // Return the username associated with the token
                return tokenEntity.getUser().getUsername();
            }
            throw new RuntimeException("Invalid token or token has expired");
        }

        // Check if the user has access to the specified App-ID
        private boolean hasAccessToAppId(String username, String appId) {
            // Find the user by username
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Check if the user has the required App-ID
                return user.getAppIds().stream()
                        .anyMatch(app -> app.getAppId().equals(appId)); // Compare with appId field

            }
            throw new RuntimeException("User not found");
        }

        // Extract the token from the Authorization header
        private String getTokenFromRequest() {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // Extract the token from the Authorization header or request parameters
                String token = request.getHeader("Authorization");
                return token != null ? token.replace("Bearer ", "") : null;
            }
            return null;
        }
    }
