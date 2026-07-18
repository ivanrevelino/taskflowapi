package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByUsername(String username);
}
