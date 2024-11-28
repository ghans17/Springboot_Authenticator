package com.argusoft.authmodule.dataInitializers;

import com.argusoft.authmodule.entities.PropertyManager;
import com.argusoft.authmodule.repositories.PropertyManagerRepository; // Make sure to create this repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PropertyManagerDataInitializer implements CommandLineRunner {

    @Autowired
    private PropertyManagerRepository propertyManagerRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if properties already exist to avoid duplicates
        if (propertyManagerRepository.count() == 0) {
            PropertyManager otpValidation = new PropertyManager();
            otpValidation.setName("otp_validation");
            otpValidation.setValue(true);
            propertyManagerRepository.save(otpValidation);

            PropertyManager otpInResponse = new PropertyManager();
            otpInResponse.setName("otp_inResponse");
            otpInResponse.setValue(true);
            propertyManagerRepository.save(otpInResponse);
        }
    }
}