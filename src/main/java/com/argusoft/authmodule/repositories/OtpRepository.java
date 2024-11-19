package com.argusoft.authmodule.repositories;

import com.argusoft.authmodule.entities.Otp;
import com.argusoft.authmodule.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByUser(User user);  // Fetch OTP for a particular user

    Optional<Otp> findByUserAndValidated(User user, boolean validated);
}

