package com.argusoft.authmodule.services;

import com.argusoft.authmodule.entities.PropertyManager;
import com.argusoft.authmodule.repositories.PropertyManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
