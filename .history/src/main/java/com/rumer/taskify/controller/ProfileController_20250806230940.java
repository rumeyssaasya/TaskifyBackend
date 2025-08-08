package com.rumer.taskify.controller;

import com.rumer.taskify.dto.UserProfile;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/profile-images/";

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        // DTO’ya dönüştür
        UserProfile dto = new UserProfile();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setProfileImageUrl(user.getProfileImageUrl());

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @RequestBody UserProfile profileDTO) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setFullName(profileDTO.getFullName());
        user.setEmail(profileDTO.getEmail());

        // Profil resmi URL’sini istersen burada güncelleyebilirsin
        if (profileDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(profileDTO.getProfileImageUrl());
        }

        userRepository.save(user);
        return ResponseEntity.ok("Profil güncellendi.");
    }

    // Şifre değiştirme endpoint’i aynen kalabilir

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

    // Profil resmi yükleme endpoint'i (multipart/form-data)

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(Authentication authentication,
                                                @RequestParam("image") MultipartFile file) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Dosya boş olamaz.");
        }

        // Dosya boyutu kontrolü (örneğin max 2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("Dosya boyutu 2MB'yi aşamaz.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        String newFilename = username + "_profile" + fileExtension;

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            User user = userOpt.get();
            user.setProfileImageUrl("/" + UPLOAD_DIR + newFilename);
            userRepository.save(user);

            return ResponseEntity.ok("Profil resmi başarıyla yüklendi.");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Dosya yüklenirken hata oluştu.");
        }
    }
}
