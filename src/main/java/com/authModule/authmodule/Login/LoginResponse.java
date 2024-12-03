package com.authModule.authmodule.Login;

public class LoginResponse {
    private String accessToken;
    private String otpCode;

    public LoginResponse(String accessToken, String otpCode) {
        this.accessToken = accessToken;
        this.otpCode = otpCode;
    }

    public LoginResponse(String accessToken) {
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
