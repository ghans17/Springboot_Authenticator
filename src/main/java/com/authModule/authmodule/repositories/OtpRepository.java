package com.authModule.authmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authModule.authmodule.entities.Otp;
import com.authModule.authmodule.entities.User;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByUser(User user);  // Fetch OTP for a particular user

    Optional<Otp> findByUserAndValidated(User user, boolean validated);
}

