package com.exmpl.authmodule.repositories;

import com.exmpl.authmodule.entities.OtpProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpPropertyRepository extends JpaRepository<OtpProperty, Long> {
    Optional<OtpProperty> findTopByOrderByIdDesc();  // Fetch the most recent OTP property settings
}
