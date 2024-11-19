package com.argusoft.authmodule.repositories;

import com.argusoft.authmodule.entities.AppID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppIdRepository extends JpaRepository<AppID, Long> {
    Optional<AppID> findByAppId(String appId);  // Find AppId by its appId
}