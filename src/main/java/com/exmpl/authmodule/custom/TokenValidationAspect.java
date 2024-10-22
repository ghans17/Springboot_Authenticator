package com.exmpl.authmodule.custom;

import com.exmpl.authmodule.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class TokenValidationAspect {

    @Autowired
    private TokenService tokenService;

    @Before("@annotation(ValidToken)")
    public void validateToken(JoinPoint joinPoint) throws Throwable {
        String token = getTokenFromRequest();
        if (token == null || !tokenService.validateAccessToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

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