package com.rumer.taskify.controller;

import com.rumer.taskify.dto.UserProfile;
import com.rumer.taskify.model.User;
import com.rumer.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

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
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        
        // ÖNEMLİ: Kullanıcının seçtiği profil resmi varsa onu kullan, yoksa cinsiyete göre default ata
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty() && 
            !user.getProfileImageUrl().contains("femaleIcon") && 
            !user.getProfileImageUrl().contains("maleIcon")) {
            // Kullanıcı özel bir resim seçmişse onu kullan
            dto.setProfileImageUrl(user.getProfileImageUrl());
        } else {
            // Cinsiyete göre default ikon ata VEYA kullanıcının önceden seçtiği ikonu koru
            if (user.getProfileImageUrl() != null && 
                (user.getProfileImageUrl().contains("femaleIcon") || 
                 user.getProfileImageUrl().contains("maleIcon"))) {
                dto.setProfileImageUrl(user.getProfileImageUrl());
            } else {
                // Yeni kullanıcı için cinsiyete göre default ata
                if ("FEMALE".equals(user.getGender())) {
                    dto.setProfileImageUrl("/images/femaleIcon.png");
                } else if ("MALE".equals(user.getGender())) {
                    dto.setProfileImageUrl("/images/maleIcon.png");
                } else {
                    dto.setProfileImageUrl("/images/default.png");
                }
            }
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UserProfile profileDTO) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setFullName(profileDTO.getFullName());
        
        // Profil resmi URL'sini güncelle - cinsiyet ikonlarını override et
        if (profileDTO.getProfileImageUrl() != null && 
            !profileDTO.getProfileImageUrl().contains("femaleIcon") && 
            !profileDTO.getProfileImageUrl().contains("maleIcon")) {
            user.setProfileImageUrl(profileDTO.getProfileImageUrl());
        }
        
        userService.saveUser(user);
        
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
}
