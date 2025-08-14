package com.rumer.taskify.controller;

import com.rumer.taskify.dto.UserProfile;
import com.rumer.taskify.model.User;
import com.rumer.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/profile-images/";

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserProfile dto = new UserProfile();
        dto.setFullName(user.getFullName());
        dto.setProfileImageUrl(user.getProfileImageUrl());

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UserProfile profileDTO) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User updatedUser = userService.updateProfile(userOpt.get(), profileDTO.getFullName(), profileDTO.getProfileImageUrl());
        return ResponseEntity.ok("Profil güncellendi.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @RequestParam String currentPassword,
                                            @RequestParam String newPassword) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean changed = userService.changePassword(userOpt.get(), currentPassword, newPassword);
        if (!changed) {
            return ResponseEntity.badRequest().body("Mevcut şifre yanlış.");
        }

        return ResponseEntity.ok("Şifre başarıyla değiştirildi.");
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(Authentication authentication,
                                                @RequestParam("image") MultipartFile file) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Dosya boş olamaz.");
        }

        if (file.getSize() > 3 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("Dosya boyutu 3MB'yi aşamaz.");
        }

        String contentType = file.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            return ResponseEntity.badRequest().body("Yalnızca JPG ve PNG formatları desteklenmektedir.");
        }

        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                return ResponseEntity.badRequest().body("Geçersiz resim dosyası.");
            }

            int width = img.getWidth();
            int height = img.getHeight();

            if (width < 200 || height < 200) {
                return ResponseEntity.badRequest().body("Resim çözünürlüğü minimum 200x200 piksel olmalı.");
            }
            if (width > 1000 || height > 1000) {
                return ResponseEntity.badRequest().body("Resim çözünürlüğü maksimum 1000x1000 piksel olmalı.");
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";

            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }

            String newFilename = username + "_profile" + fileExtension;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            User user = userOpt.get();
            user.setProfileImageUrl("/" + UPLOAD_DIR + newFilename);
            userService.saveUser(user);

            return ResponseEntity.ok("Profil resmi başarıyla yüklendi.");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Dosya işlenirken hata oluştu.");
        }
    }
}
