package com.authModule.authmodule.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authModule.authmodule.entities.PropertyManager;

import java.util.Optional;

@Repository
public interface PropertyManagerRepository extends JpaRepository<PropertyManager, Long> {

    // Fetch the most recent property based on name
    Optional<PropertyManager> findTopByNameOrderByIdDesc(String name);

}

