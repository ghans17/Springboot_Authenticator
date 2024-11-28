package com.argusoft.authmodule.repositories;
import com.argusoft.authmodule.entities.UserOtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOtpStatusRepository extends JpaRepository<UserOtpStatus, String> {

}
