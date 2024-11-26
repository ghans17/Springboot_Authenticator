package com.argusoft.authmodule.services;
import com.argusoft.authmodule.entities.Otp;
import com.argusoft.authmodule.entities.PropertyManager;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.OtpRepository;
import com.argusoft.authmodule.repositories.PropertyManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {


    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PropertyManagerRepository propertyManagerRepository;

    @Autowired
    private EmailService emailService;

    // Generate OTP for user
    public String generateOtpForUser(User user) {
        // Check if an unvalidated OTP already exists for this user
        Optional<Otp> existingOtp = otpRepository.findByUserAndValidated(user, false);
        if (existingOtp.isPresent()) {
            Otp otpEntry = existingOtp.get();
            // Check if the existing OTP has expired
            if (otpEntry.getExpiresAt().isAfter(LocalDateTime.now())) {
                // If OTP is still valid, return the existing OTP
                return otpEntry.getOtpCode();
            } else {
                // If OTP is expired, delete it
                otpRepository.delete(otpEntry);
            }
        }

        // If no unvalidated OTP exists, generate a new one
        String otp = String.format("%06d", new Random().nextInt(1000000)); // Generates a 6-digit OTP
        Otp otpEntry = new Otp();
        otpEntry.setUser(user);
        otpEntry.setOtpCode(otp);
        otpEntry.setValidated(false);  // Set to false initially
        otpEntry.setCreatedAt(LocalDateTime.now());
        otpEntry.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntry); // Save the OTP

        // Send OTP email
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("otp", otp);
        emailService.queueEmail(user.getEmail(), "OTP_EMAIL", placeholders);

        return otp;
    }

    // Validate OTP for user
    public boolean validateOtp(User user, String otp) {
        Optional<Otp> otpOptional = otpRepository.findByUser(user);

        if (otpOptional.isPresent()) {
            Otp otpEntry = otpOptional.get();
            // Check if the OTP has expired
            if (otpEntry.getExpiresAt().isBefore(LocalDateTime.now())) {
                otpRepository.delete(otpEntry); // Delete expired OTP
                return false;
            }

            // Check if OTP matches
            if (otpEntry.getOtpCode().equals(otp)) {
                otpEntry.setValidated(true); // Mark OTP as validated
                otpRepository.save(otpEntry);
                return true;
            }
        }
        return false;
    }

    // Check if OTP validation is required for login
    public boolean isOtpRequiredForLogin() {
        Optional<PropertyManager> otpPropertyOptional = propertyManagerRepository.findTopByNameOrderByIdDesc("otp_validation");

        if (otpPropertyOptional.isPresent()) {
            return otpPropertyOptional.get().getValue();
        } else {
            return false;
        }
    }

    // Check if OTP should be included in the response (for debugging)
    public boolean isOtpInResponse() {
        Optional<PropertyManager> otpPropertyOptional = propertyManagerRepository.findTopByNameOrderByIdDesc("otp_inResponse");

        if (otpPropertyOptional.isPresent()) {
            return otpPropertyOptional.get().getValue();
        } else {
            return false;
        }
    }
}







    // Generate a random OTP
//    private String generateRandomOTP() {
//        int length = 6;
//        String characters = "0123456789";
//        StringBuilder otp = new StringBuilder();
//        Random ran = new Random();
//        for (int i = 0; i < length; i++) {
//            otp.append(characters.charAt(ran.nextInt(characters.length())));
//        }
//        return otp.toString();
//    }



