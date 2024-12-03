package com.authModule.authmodule.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.authModule.authmodule.entities.OtpSettings;

import java.util.Optional;

public interface OtpSettingsRepository extends JpaRepository<OtpSettings,Long> {

//    Optional<OtpSettings> findById();
    Optional<OtpSettings> findTopByOrderByIdDesc(); // Fetch latest OTP settings
}


