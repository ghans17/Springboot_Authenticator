package com.argusoft.authmodule.passwordSetup;


import com.argusoft.authmodule.entities.PasswordSetupToken;
import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.services.PasswordSetupService;
import com.argusoft.authmodule.services.UserService;
import com.argusoft.authmodule.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PasswordService {

    @Autowired
    private PasswordSetupService passwordSetupService;

    @Autowired
    private UserService userService;



    public void passwordSetup(PasswordSetupRequest request){
        PasswordSetupToken setupToken = passwordSetupService.validateToken(request.getToken());
        if (setupToken == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        // Retrieve the user associated with the token
        User user = setupToken.getUser();
        if (user == null) {
            throw new NoSuchElementException("User not found for this token");
        }

        user.setPassword(PasswordUtil.hashPassword(request.getNewPassword()));
        userService.saveUser(user);

        passwordSetupService.markTokenAsUsed(request.getToken());
    }
}
