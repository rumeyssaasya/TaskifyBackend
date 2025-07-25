package com.rumer.taskify.repository;

import com.rumer.taskify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByUsername(String username); // login için gerekli
     Optional<User> findByVerificationToken(String token);  // Email doğrulama için
}
