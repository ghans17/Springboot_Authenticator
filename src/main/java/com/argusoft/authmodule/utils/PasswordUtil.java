package com.argusoft.authmodule.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


public class PasswordUtil {

    // Hash the password using BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Check if the raw password matches the hashed password
    public static boolean matchPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);

        //BCrypt includes the salt in the hashed password, so when checkpw is called,
        // it retrieves the salt,hashes the raw password again, and
        // compares the resulting hash to the stored hash.
    }
}

