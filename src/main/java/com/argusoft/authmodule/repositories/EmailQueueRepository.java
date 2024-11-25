package com.argusoft.authmodule.repositories;

import com.argusoft.authmodule.entities.EmailQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {
    List<EmailQueue> findByIsProcessedFalse(); // Find unprocessed emails
}
