package com.argusoft.authmodule.register;

import com.argusoft.authmodule.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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