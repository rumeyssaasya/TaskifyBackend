package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Profil bilgilerini getir (kendi profili)
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        // Şifre hariç profil bilgilerini dönebilirsin
        // İstersen özel DTO yapabilirsin, şu an direkt gönderiyorum
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // 2. Profil güncelleme (fullName, email, profileImageUrl vb.)
    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody User updatedUser) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setFullName(updatedUser.getFullName());
        user.setEmail(updatedUser.getEmail());
        user.setProfileImageUrl(updatedUser.getProfileImageUrl());

        userRepository.save(user);
        return ResponseEntity.ok("Profil güncellendi.");
    }

    // 3. Şifre değiştirme
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @RequestParam String currentPassword,
                                            @RequestParam String newPassword) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Mevcut şifre yanlış.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Şifre başarıyla değiştirildi.");
    }
}
