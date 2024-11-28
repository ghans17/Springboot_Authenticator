package com.argusoft.authmodule.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean validated;  // To indicate whether OTP is validated or not

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private int retryCount;

    private String emailOtp;
    private String smsOtp;
}
