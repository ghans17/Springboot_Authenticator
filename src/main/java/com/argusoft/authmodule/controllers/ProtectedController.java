package com.argusoft.authmodule.controllers;


import com.argusoft.authmodule.custom.CheckAppIdAccess;
import com.argusoft.authmodule.custom.ValidToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @ValidToken
    @CheckAppIdAccess("PROJECT_X") // Require "PROJECT_X" App-ID to access this endpoint
    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedData() {
        return ResponseEntity.ok("This is protected data for Project X");
    }


//    @ValidToken

    @ValidToken(message = "Access token is required for /protected-data!")
    @CheckAppIdAccess("PROJECT_Y") // Require "PROJECT_Y" App-ID to access this endpoint
    @GetMapping("/protected-data2")
    public ResponseEntity<String> getProtectedData2() {
        return ResponseEntity.ok("This is protected data for Project Y");
    }
}
