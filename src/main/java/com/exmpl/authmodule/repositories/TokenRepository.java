package com.exmpl.authmodule.repositories;

import com.exmpl.authmodule.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessTokenHash(String accessTokenHash); // Find by hashed access token

    Optional<Token> findByAccessToken(String accessToken);
}


