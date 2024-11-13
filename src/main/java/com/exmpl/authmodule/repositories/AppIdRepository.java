package com.exmpl.authmodule.repositories;

import com.exmpl.authmodule.entities.AppID;
import com.exmpl.authmodule.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppIdRepository extends JpaRepository<AppID, Long> {
    Optional<AppID> findByAppId(String appId);  // Find AppId by its appId
}