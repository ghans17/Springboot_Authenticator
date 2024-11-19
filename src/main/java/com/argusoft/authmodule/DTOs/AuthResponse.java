package com.argusoft.authmodule.DTOs;

public class AuthResponse {
    private String accessToken;
    private String otpCode;

    public AuthResponse(String accessToken, String otpCode) {
        this.accessToken = accessToken;
        this.otpCode = otpCode;
    }

    public AuthResponse(String accessToken) {
        this.accessToken=accessToken;
    }

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
