package com.argusoft.authmodule.services;
import com.argusoft.authmodule.entities.Otp;
import com.argusoft.authmodule.entities.OtpProperty;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.OtpPropertyRepository;
import com.argusoft.authmodule.repositories.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {


//    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private OtpPropertyRepository otpPropertyRepository;

    @Autowired
    private EmailService emailService;

    // Generate OTP for user
    public String generateOtpForUser(User user) {
        // Check if an unvalidated OTP already exists for this user
        Optional<Otp> existingOtp = otpRepository.findByUserAndValidated(user, false);
        if (existingOtp.isPresent()) {
            // If an OTP already exists and hasn't been validated, return the existing OTP
            return existingOtp.get().getOtpCode();
        }

        // If no unvalidated OTP exists, generate a new one
        String otp = String.format("%06d", new Random().nextInt(1000000)); // Generates a 6-digit OTP
        Otp otpEntry = new Otp();
        otpEntry.setUser(user);
        otpEntry.setOtpCode(otp);
        otpEntry.setValidated(false);  // Set to false initially
        otpEntry.setCreatedAt(LocalDateTime.now());

        otpRepository.save(otpEntry); // Save the OTP

        // Send OTP email
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("otp", otp);
        emailService.sendEmail(user.getEmail(), "OTP_EMAIL", placeholders);

        return otp;
    }

    // Validate OTP for user
    public boolean validateOtp(User user, String otp) {
        Optional<Otp> otpOptional = otpRepository.findByUser(user);
        if (otpOptional.isPresent() && otpOptional.get().getOtpCode().equals(otp)) {
            Otp otpEntry = otpOptional.get();
//            otpEntry.setValidated(true);  // Mark as validated
//            otpRepository.save(otpEntry);
            otpRepository.delete(otpEntry);
            return true;
        }
        return false;
    }

    // Check if OTP validation is required for login
    public boolean isOtpRequiredForLogin() {
        Optional<OtpProperty> otpPropertyOptional = otpPropertyRepository.findTopByOrderByIdDesc();
        return otpPropertyOptional.map(OtpProperty::isValidation).orElse(false);
    }

    // Check if OTP should be included in the response (for debugging)
    public boolean isOtpInResponse() {
        Optional<OtpProperty> otpPropertyOptional = otpPropertyRepository.findTopByOrderByIdDesc();
        return otpPropertyOptional.map(OtpProperty::isInResponse).orElse(false);
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



