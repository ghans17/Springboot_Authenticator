package com.authModule.authmodule.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.authModule.authmodule.entities.UserOtpStatus;

public interface UserOtpStatusRepository extends JpaRepository<UserOtpStatus, String> {

}
