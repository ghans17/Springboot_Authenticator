package com.argusoft.authmodule.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "otp_properties")
public class OtpProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean validation;  // Whether OTP validation is required for login

    @Column(nullable = false)
    private boolean inResponse;  // Whether OTP should be included in response for debugging

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isValidation() { return validation; }
    public void setValidation(boolean validation) { this.validation = validation; }

    public boolean isInResponse() { return inResponse; }
    public void setInResponse(boolean inResponse) { this.inResponse = inResponse; }
}

