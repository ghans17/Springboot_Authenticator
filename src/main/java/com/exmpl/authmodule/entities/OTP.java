package com.exmpl.authmodule.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private boolean validation;  // Indicates whether OTP is required for login

    @Column(nullable = false)
    private boolean inResponse; // This indicates whether the OTP should be visible in the response for debugging

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public boolean isInResponse() {
        return inResponse;
    }

    public void setInResponse(boolean inResponse) {
        this.inResponse = inResponse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
