package com.exmpl.authmodule.services;


import com.exmpl.authmodule.DTOs.LoginRequest;
import com.exmpl.authmodule.entities.User;
import com.exmpl.authmodule.repositories.UserRepository;
import com.exmpl.authmodule.utils.JwtUtil;
import com.exmpl.authmodule.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TokenService tokenService;

    //create user
    public User registerUser(User user) {
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    //authenticate and generate token
//    public String authenticateUser(LoginRequest loginRequest) {
//        User user = findByUsername(loginRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!PasswordUtil.matchPassword(loginRequest.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        String accessToken = JwtUtil.generateAccessToken(user.getUsername());
//        String hashedAccessToken = tokenService.hashToken(accessToken);
//        tokenService.generateTokens(user, accessToken, hashedAccessToken);
//
//        return hashedAccessToken;
//    }


}
