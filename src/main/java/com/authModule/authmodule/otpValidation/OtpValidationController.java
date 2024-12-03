package com.authModule.authmodule.otpValidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class OtpValidationController {

    @Autowired
    private OtpValidationService otpValidationService;

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        try {
            return ResponseEntity.ok(otpValidationService.validateOtp(otpValidationRequest));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}

