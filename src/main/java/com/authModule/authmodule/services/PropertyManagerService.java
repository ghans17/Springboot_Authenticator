package com.authModule.authmodule.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.entities.PropertyManager;
import com.authModule.authmodule.repositories.PropertyManagerRepository;

import java.util.Optional;

@Service
public class PropertyManagerService {

    @Autowired
    private PropertyManagerRepository propertyManagerRepository;

    public void updateOtpProperty(String name, Boolean value) {
        // Find the existing property or create a new one
        Optional<PropertyManager> existingProperty = propertyManagerRepository.findTopByNameOrderByIdDesc(name);

        PropertyManager property = existingProperty.orElse(new PropertyManager());
        property.setName(name);
        property.setValue(value);

        propertyManagerRepository.save(property);
    }

}
