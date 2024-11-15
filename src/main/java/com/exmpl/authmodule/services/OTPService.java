package com.exmpl.authmodule.services;

import com.exmpl.authmodule.entities.OTP;
import com.exmpl.authmodule.repositories.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    // Generate OTP and save it
    public OTP generateOTP(String username) {
        String otpCode = generateRandomOTP();
        OTP otp = new OTP();
        otp.setUsername(username);
        otp.setOtpCode(otpCode);
        otp.setValidation(true);  // Enable OTP validation for this user (you can control this based on your business logic)
        otp.setInResponse(true);   // Set to true for debugging (to send in response for frontend development)
        otp.setCreatedAt(LocalDateTime.now());
        return otpRepository.save(otp);
    }

    // Validate OTP
    public boolean validateOTP(String username, String otpCode) {
        Optional<OTP> otpOptional = otpRepository.findByUsername(username);
        if (otpOptional.isPresent()) {
            OTP otp = otpOptional.get();
            // Check if OTP matches and hasn't expired (1-minute validity, for example)
            if (otp.getOtpCode().equals(otpCode)) {
//                 && otp.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))
                // Mark as validated (not used in your scenario since validation column is not needed anymore)
                otpRepository.save(otp);
                return true;
            }
        }
        return false;
    }

    // Generate a random OTP
    private String generateRandomOTP() {
        int length = 6;
        String characters = "0123456789";
        StringBuilder otp = new StringBuilder();
        Random ran = new Random();
        for (int i = 0; i < length; i++) {
            otp.append(characters.charAt(ran.nextInt(characters.length())));
        }
        return otp.toString();
    }

    public Optional<OTP> findByUsername(String username) {
        return otpRepository.findByUsername(username);
    }
}
