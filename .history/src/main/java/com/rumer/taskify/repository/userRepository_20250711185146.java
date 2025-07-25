package com.rumer.taskify.repository;

import com.rumer.taskify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<User, Long> {
     Optional<User> findByUsername(String username); // login için gerekli
}
