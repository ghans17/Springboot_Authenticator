package com.authModule.authmodule.register;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.entities.User;
import com.authModule.authmodule.services.PasswordSetupService;
import com.authModule.authmodule.services.UserService;

@Service
public class RegService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordSetupService passwordSetupService;

    @Transactional
    public void register(User user) {
        try {
            if (!userService.isEmailUnique(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            if (!userService.isUsernameUnique(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            User savedUser = userService.registerUser(user);
            passwordSetupService.createPasswordSetupToken(savedUser);
        } catch (Exception ex) {
            throw new RuntimeException("Registration failed. Please try again.");
        }
    }
}
