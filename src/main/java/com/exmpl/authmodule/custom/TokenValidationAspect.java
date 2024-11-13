package com.exmpl.authmodule.custom;

import com.exmpl.authmodule.entities.Token;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.AppIdRepository;
import com.exmpl.authmodule.services.TokenService;
import com.exmpl.authmodule.services.UserService;
import com.exmpl.authmodule.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;
@Aspect
@Component
@Order(1)
public class TokenValidationAspect {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppIdRepository appIdRepository;  // We need this to check App-ID


        @Before("@annotation(validToken)")
        public void validateToken(JoinPoint joinPoint, ValidToken validToken) throws Throwable {
            String token = getTokenFromRequest();

            // Validate the token
            if (token == null || !tokenService.validateAccessToken(token)) {
                throw new RuntimeException(validToken.message());
            }

            // If valid, extend the expiration time
            Optional<Token> optionalExistingToken = tokenService.findByAccessTokenHash(token);
            if (optionalExistingToken.isPresent()) {
                Token existingToken = optionalExistingToken.get(); // Extract the Token from Optional
                existingToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Extend expiry
                tokenService.save(existingToken); // Save updated token
            }
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
