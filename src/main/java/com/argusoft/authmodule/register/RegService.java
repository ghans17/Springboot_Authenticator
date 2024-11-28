package com.argusoft.authmodule.register;


import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.services.PasswordSetupService;
import com.argusoft.authmodule.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordSetupService passwordSetupService;

    public void register(User user){
        // Save the new user
        User savedUser = userService.registerUser(user);

        // Create a password setup token and send email
        passwordSetupService.createPasswordSetupToken(savedUser);
    }
}
