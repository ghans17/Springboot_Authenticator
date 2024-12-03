package com.authModule.authmodule.dataInitializers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.authModule.authmodule.entities.EmailTemplate;
import com.authModule.authmodule.repositories.EmailTemplateRepository;

@Component
public class EmailTemplateDataInitializer implements CommandLineRunner {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is already populated
        if (emailTemplateRepository.count() == 0) {
            // Create and save  email templates
            EmailTemplate otpEmailTemplate = new EmailTemplate();
            otpEmailTemplate.setName("OTP_EMAIL");
            otpEmailTemplate.setSubject("Your OTP Code");
            otpEmailTemplate.setBody("Your OTP code is {{otp}}. Please use it to complete your login.");
            // otpEmailTemplate.setRecipientEmail("noreply@yourapp.com"); // Set a default recipient email

            EmailTemplate passwordSetupTemplate = new EmailTemplate();
            passwordSetupTemplate.setName("PASSWORD_SETUP_EMAIL");
            passwordSetupTemplate.setSubject("Password Setup Request");
            passwordSetupTemplate.setBody("To set up your password, please click the following link: {{setupLink}}");
            // passwordSetupTemplate.setRecipientEmail("noreply@yourapp.com"); // Set a default recipient email

            // Save templates to the repository
            emailTemplateRepository.save(otpEmailTemplate);
            emailTemplateRepository.save(passwordSetupTemplate);

            System.out.println("Dummy email templates created successfully.");
        } else {
            System.out.println("Email templates already exist in the database.");
        }
    }
}