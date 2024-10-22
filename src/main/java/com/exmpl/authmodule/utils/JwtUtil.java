package com.exmpl.authmodule.utils;

import com.exmpl.authmodule.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.security.MessageDigest;
import java.util.Base64;

public class JwtUtil {

    private static final String SECRET_KEY = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";  // Use a stronger key in production

    public static String generateAccessToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // Hash the JWT token using SHA-256 before returning it
        return hashToken(token);
    }

    public static String generateRefreshToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 1 day
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // Hash the JWT token using SHA-256 before returning it
        return hashToken(token);
    }

    public static String extractUsername(String token) {
        token = decodeToken(token);  // Decode before processing
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean isTokenExpired(String token) {
        token = decodeToken(token);  // Decode before processing
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // Validate token against the user entity
    public static boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);  // Convert the hash to Base64 string for storage
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
//            // Convert the hash to a hexadecimal string instead of Base64
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hashBytes) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            return hexString.toString(); // Return the hashed token as a hexadecimal string
//        } catch (Exception e) {
//            throw new RuntimeException("Error hashing token", e);
//        }
    }

    private static String decodeToken(String encodedToken) {
        // Decode the Base64 encoded token before processing
        byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
        return new String(decodedBytes);
    }
}
