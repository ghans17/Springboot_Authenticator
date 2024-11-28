package com.argusoft.authmodule.services;


import com.argusoft.authmodule.entities.User;
import com.argusoft.authmodule.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
