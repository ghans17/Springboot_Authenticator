package com.authModule.authmodule.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.entities.*;
import com.authModule.authmodule.repositories.OtpRepository;
import com.authModule.authmodule.repositories.OtpSettingsRepository;
import com.authModule.authmodule.repositories.PropertyManagerRepository;
import com.authModule.authmodule.repositories.UserOtpStatusRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PropertyManagerRepository propertyManagerRepository;

    @Autowired
    private OtpSettingsRepository otpSettingsRepository;

    @Autowired
    private UserOtpStatusRepository userOtpStatusRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    // Generate OTP for user
    public String generateOtpForUser(User user) {
        OtpSettings otpSettings = otpSettingsRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("OTP settings not configured"));

        String userIdentifier = user.getEmail();

        Optional<UserOtpStatus> statusOpt = userOtpStatusRepository.findById(userIdentifier);
        if (statusOpt.isPresent()) {
            UserOtpStatus status = statusOpt.get();
            // Check if user is locked out
            if (status.getLockoutEndTime() != null && LocalDateTime.now().isBefore(status.getLockoutEndTime())) {
                throw new RuntimeException("User is locked out until " + status.getLockoutEndTime());
            }
            // Reset retry count and lockoutEndTime if lockout period has passed
            if (status.getLockoutEndTime() != null && LocalDateTime.now().isAfter(status.getLockoutEndTime())) {
                status.setOtpRetryCount(0);
                status.setLockoutEndTime(null);
            }
        }

        // Initialize variables
        String emailOtp = null;
        String smsOtp = null;

        // Check for existing unvalidated OTP
        Optional<Otp> existingOtpOpt = otpRepository.findByUserAndValidated(user, false);

        if (existingOtpOpt.isPresent()) {
            // Use existing OTPs
            Otp existingOtp = existingOtpOpt.get();
            if (existingOtp.getExpiresAt().isAfter(LocalDateTime.now())) {
                emailOtp = existingOtp.getEmailOtp();
                smsOtp = existingOtp.getSmsOtp();
            } else {
                // If OTP is expired, delete it
                otpRepository.delete(existingOtp);
            }
        }

        // If OTPs were not initialized, generate new OTPs
        if (emailOtp == null || smsOtp == null) {
            emailOtp = generateOtp(otpSettings.getOtpLength(), otpSettings.getOtpType());
            smsOtp = otpSettings.isDifferentForPhoneEmail()
                    ? generateOtp(otpSettings.getOtpLength(), otpSettings.getOtpType())
                    : emailOtp;

            // Save the new OTPs in the database
            Otp otpEntry = new Otp();
            otpEntry.setUser(user);
            otpEntry.setEmailOtp(emailOtp);
            otpEntry.setSmsOtp(smsOtp);
            otpEntry.setValidated(false);
            otpEntry.setCreatedAt(LocalDateTime.now());
            otpEntry.setExpiresAt(LocalDateTime.now().plusMinutes(otpSettings.getOtpExpirationTime()));

            otpRepository.save(otpEntry);
        }

        // Send OTP via Email
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("otp", emailOtp);
        emailService.queueEmail(user.getEmail(), "OTP_EMAIL", placeholders);

        // Send OTP via SMS
        if (user.getPhone() != null) {
            String message = "Your OTP is: " + smsOtp + ". It is valid for " + otpSettings.getOtpExpirationTime() + " minutes.";
            smsService.sendSms(user.getPhone(), message);
        }

        // Update user OTP status
        UserOtpStatus status = statusOpt.orElse(new UserOtpStatus());
        status.setUserIdentifier(userIdentifier);
        status.setOtpRetryCount(status.getOtpRetryCount() + 1);

        //check if retry limit is reached, then set lockout
        int otpRetryLimit = otpSettings.getOtpRetryLimit();
        if (status.getOtpRetryCount() > otpRetryLimit) {
            LocalDateTime lockoutUntil = LocalDateTime.now().plusSeconds(otpSettings.getLockoutTime());
            status.setLockoutEndTime(lockoutUntil);
            userOtpStatusRepository.save(status);
            throw new RuntimeException("User locked out until " + lockoutUntil);
        }

        userOtpStatusRepository.save(status);
        return emailOtp; // Returning email OTP as default for confirmation/debugging
    }


    // Generate OTP based on type (numeric or alphanumeric)
    private String generateOtp(int length, String type) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        if ("numeric".equalsIgnoreCase(type)) {
            String characters = "0123456789";
            for (int i = 0; i < length; i++) {
                otp.append(characters.charAt(random.nextInt(characters.length())));
            }
        } else if ("alphanumeric".equalsIgnoreCase(type)) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for (int i = 0; i < length; i++) {
                otp.append(characters.charAt(random.nextInt(characters.length())));
            }
        } else {
            throw new IllegalArgumentException("Invalid OTP type");
        }

        return otp.toString();
    }

    public boolean validateOtp(User user, String otp) {
        String userIdentifier = user.getEmail();

        Optional<UserOtpStatus> statusOpt = userOtpStatusRepository.findById(userIdentifier);
        UserOtpStatus status = statusOpt.orElse(new UserOtpStatus());
        status.setUserIdentifier(userIdentifier);

        // Check if user is locked out
        if (status.getLockoutEndTime() != null && LocalDateTime.now().isBefore(status.getLockoutEndTime())) {
            throw new RuntimeException("User is locked out until " + status.getLockoutEndTime());
        }
        // Reset retry count and lockoutEndTime if lockout period has passed
        if (status.getLockoutEndTime() != null && LocalDateTime.now().isAfter(status.getLockoutEndTime())) {
            status.setOtpRetryCount(0);
            status.setLockoutEndTime(null);
        }

        // Retrieve unvalidated OTP
        Optional<Otp> otpOptional = otpRepository.findByUserAndValidated(user, false);
        if (otpOptional.isEmpty()) {
            // No OTP exists, increment retry count
            status.setOtpRetryCount(status.getOtpRetryCount() + 1);

            //set retry limit
            int otpRetryLimit = otpSettingsRepository.findTopByOrderByIdDesc()
                    .map(OtpSettings::getOtpRetryLimit)
                    .orElse(3); // Default retry limit

            //check if retry limit is reached, then set lockout
            if (status.getOtpRetryCount() > otpRetryLimit) {
                int lockoutDuration = otpSettingsRepository.findTopByOrderByIdDesc()
                        .map(OtpSettings::getLockoutTime)
                        .orElse(300); // Default lockout duration
                LocalDateTime lockoutUntil = LocalDateTime.now().plusSeconds(lockoutDuration);
                status.setLockoutEndTime(lockoutUntil);
                userOtpStatusRepository.save(status);
                throw new RuntimeException("User locked out until " + lockoutUntil);
            }

            userOtpStatusRepository.save(status);
            return false;
        }

        // Validate OTP
        Otp otpEntry = otpOptional.get();
        if (otpEntry.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntry); // Expired OTP
            return false;
        }

        if (otpEntry.getEmailOtp().equals(otp) || otpEntry.getSmsOtp().equals(otp)) {
            otpRepository.delete(otpEntry); // Valid OTP, delete entry
            status.setOtpRetryCount(0); // Reset retry count
            status.setLockoutEndTime(null);
            userOtpStatusRepository.save(status);
            return true;
        } else {
            //increment retry count
            status.setOtpRetryCount(status.getOtpRetryCount() + 1);

            //set limit
            int otpRetryLimit = otpSettingsRepository.findTopByOrderByIdDesc()
                    .map(OtpSettings::getOtpRetryLimit)
                    .orElse(3);

            //check if retry limit is reached, then set lockout
            if (status.getOtpRetryCount() > otpRetryLimit) {
                int lockoutDuration = otpSettingsRepository.findTopByOrderByIdDesc()
                        .map(OtpSettings::getLockoutTime)
                        .orElse(300);
                LocalDateTime lockoutUntil = LocalDateTime.now().plusSeconds(lockoutDuration);
                status.setLockoutEndTime(lockoutUntil);
                userOtpStatusRepository.save(status);
                throw new RuntimeException("User locked out until " + lockoutUntil);
            }

            userOtpStatusRepository.save(status);
            return false;
        }
    }

    // Check if OTP validation is required for login
    public boolean isOtpRequiredForLogin() {
        Optional<PropertyManager> otpPropertyOptional = propertyManagerRepository
                .findTopByNameOrderByIdDesc("otp_validation");

        if (otpPropertyOptional.isPresent()) {
            return otpPropertyOptional.get().getValue() != null && otpPropertyOptional.get().getValue();
        } else {
            return false;
        }
    }

    // Check if OTP should be included in the response (for debugging)
    public boolean isOtpInResponse() {
        Optional<PropertyManager> otpPropertyOptional = propertyManagerRepository
                .findTopByNameOrderByIdDesc("otp_inResponse");

        if (otpPropertyOptional.isPresent()) {
            return otpPropertyOptional.get().getValue() != null && otpPropertyOptional.get().getValue();
        } else {
            return false;
        }
    }
}