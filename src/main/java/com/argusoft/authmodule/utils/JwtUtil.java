package com.argusoft.authmodule.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";  // Use a stronger key in production

    //generate Jwt access token
    public static String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact(); // Return plain JWT token
    }

}