package com.authModule.authmodule.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "otp_settings")
public class OtpSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_length", nullable = false)
    private int otpLength; // Length of OTP (e.g., 6 digits)

    @Column(name = "otp_type", nullable = false)
    private String otpType; // "numeric" or "alphanumeric"

    @Column(name = "otp_expiration_time", nullable = false)
    private int otpExpirationTime; // Expiration time in minutes

    @Column(name = "is_different_for_phone_email", nullable = false)
    private boolean isDifferentForPhoneEmail; // Whether OTP is different for phone and email

    @Column(name = "otp_retry_limit", nullable = false)
    private int otpRetryLimit; // retry limit for a user

    @Column(name = "lockout_time", nullable = false)
    private int lockoutTime; // lockout time for a user is retry limit is reached


}
