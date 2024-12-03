package com.authModule.authmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authModule.authmodule.entities.PasswordSetupToken;

import java.util.Optional;

public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupToken, Long> {
    Optional<PasswordSetupToken> findByToken(String token);
}
