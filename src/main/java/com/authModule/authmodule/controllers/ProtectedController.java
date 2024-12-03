package com.authModule.authmodule.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authModule.authmodule.custom.ValidateAccess;

@RestController
public class ProtectedController {

    @ValidateAccess(appId = "PROJECT_X")  // Require "PROJECT_X" App-ID to access this endpoint
    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedData() {
        return ResponseEntity.ok("This is protected data for Project X");
    }



    @ValidateAccess(message = "Access Denied", appId ="PROJECT_Y")  // Require "PROJECT_Y" App-ID to access this endpoint
    @GetMapping("/protected-data2")
    public ResponseEntity<String> getProtectedData2() {
        return ResponseEntity.ok("This is protected data for Project Y");
    }
}
