package com.authModule.authmodule.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.authModule.authmodule.entities.User;
import com.authModule.authmodule.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TokenService tokenService;

    //create user
    public User registerUser(User user) {
        user.setPassword(null);
        return userRepository.save(user);
    }

    public boolean isEmailUnique(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean isUsernameUnique(String username) {
        return !userRepository.existsByUsername(username);
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
