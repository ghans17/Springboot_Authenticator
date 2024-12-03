package com.authModule.authmodule.passwordSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class PasswordSetupController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/password-setup")
    public ResponseEntity<?> completePasswordSetup(@RequestBody PasswordSetupRequest request) {

        try {
            passwordService.passwordSetup(request);
            return ResponseEntity.ok("Password has been successfully updated.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
