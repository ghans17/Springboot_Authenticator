package com.authModule.authmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authModule.authmodule.entities.AppID;

import java.util.Optional;

@Repository
public interface AppIdRepository extends JpaRepository<AppID, Long> {
    Optional<AppID> findByAppId(String appId);  // Find AppId by its appId
}