package com.rumer.taskify.repository;

import com.rumer.taskify.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<user, Long> {
     Optional<user> findByUsername(String username); // login için gerekli
}
