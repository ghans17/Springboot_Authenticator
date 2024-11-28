package com.argusoft.authmodule.repositories;


import com.argusoft.authmodule.entities.OtpSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpSettingsRepository extends JpaRepository<OtpSettings,Long> {

//    Optional<OtpSettings> findById();
    Optional<OtpSettings> findTopByOrderByIdDesc(); // Fetch latest OTP settings
}


