package com.authModule.authmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authModule.authmodule.entities.EmailQueue;

import java.util.List;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {
    List<EmailQueue> findByIsProcessedFalse(); // Find unprocessed emails
}
