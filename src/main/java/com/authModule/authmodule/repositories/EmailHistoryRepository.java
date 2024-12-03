package com.authModule.authmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authModule.authmodule.entities.EmailHistory;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {
}

