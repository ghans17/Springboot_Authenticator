package com.argusoft.authmodule.DTOs;

public class OtpValidationRequest {

    private String username;
    private String otpCode;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
}
