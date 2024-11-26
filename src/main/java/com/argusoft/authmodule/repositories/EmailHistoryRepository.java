package com.argusoft.authmodule.repositories;

import com.argusoft.authmodule.entities.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {
}

