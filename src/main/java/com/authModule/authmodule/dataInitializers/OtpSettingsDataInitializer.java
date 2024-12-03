package com.authModule.authmodule.dataInitializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.authModule.authmodule.entities.OtpSettings;
import com.authModule.authmodule.repositories.OtpSettingsRepository;

@Component
public class OtpSettingsDataInitializer implements CommandLineRunner {
    @Autowired
    private OtpSettingsRepository otpSettingsRepository;


    @Override
    public void run(String... args) throws Exception {
        // Check if OTP settings already exist to avoid inserting duplicate data
        if (otpSettingsRepository.count() == 0) {
            // Create default OTP settings
            OtpSettings otpSettings = new OtpSettings();
            otpSettings.setOtpLength(6);  // Default OTP length (6 digits)
            otpSettings.setOtpType("numeric");  // Default OTP type (numeric)
            otpSettings.setOtpExpirationTime(5);  // Default expiration time (5 minutes)
            otpSettings.setDifferentForPhoneEmail(true);  // OTP is the same for both phone and email by default
            otpSettings.setLockoutTime(300);
            otpSettings.setOtpRetryLimit(4);
            // Save to the repository
            otpSettingsRepository.save(otpSettings);
            System.out.println("Default OTP settings initialized.");
        } else {
            System.out.println("OTP settings already exist.");
        }
    }
}