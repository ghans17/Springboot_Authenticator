package com.exmpl.authmodule.DTOs;

public class AuthResponse {
    private String accessToken;
//    private String refreshToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
    }

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
//
//    public String getRefreshToken() {
//        return refreshToken;
//    }
//
//    public void setRefreshToken(String refreshToken) {
//        this.refreshToken = refreshToken;
//    }
}
