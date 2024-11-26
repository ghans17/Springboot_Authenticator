package com.argusoft.authmodule.repositories;


import com.argusoft.authmodule.entities.PropertyManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyManagerRepository extends JpaRepository<PropertyManager, Long> {

    // Fetch the most recent property based on name
    Optional<PropertyManager> findTopByNameOrderByIdDesc(String name);

}

