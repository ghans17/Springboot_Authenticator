package com.exmpl.authmodule.controllers;

import com.exmpl.authmodule.custom.ValidToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @ValidToken
    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedData() {
        return ResponseEntity.ok("This is protected data");
    }
}
