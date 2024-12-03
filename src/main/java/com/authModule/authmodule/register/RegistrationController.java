package com.authModule.authmodule.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.authModule.authmodule.entities.User;


@CrossOrigin
@RestController
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private RegService regService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        regService.register(user);
        return ResponseEntity.ok(new RegisterResponse("Registration successful. Password setup link has been sent to your email."));
    }
}