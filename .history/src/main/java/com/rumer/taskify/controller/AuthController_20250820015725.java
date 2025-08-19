package com.rumer.taskify.controller;


import com.rumer.taskify.service.UserService;

import io.jsonwebtoken.io.IOException;

import com.rumer.taskify.dto.ForgotPasswordRequest;
import com.rumer.taskify.dto.ResetPasswordRequest;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.security.JwtUtils;
import com.rumer.taskify.service.EmailService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          EmailService emailService,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;   
        this.userService = userService;
    }
    // Şifre validasyon metodu
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+[]{}|;:,.<>?".indexOf(ch) >= 0);
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    // Kullanıcı kayıt endpoint'i
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Kullanıcı adı kontrolü
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username Kullanılmış!");
        }

        String password = user.getPassword();

        // Şifre validasyonları
        if (!isValidPassword(password)) {
            return ResponseEntity.badRequest().body(
                "Şifre en az 8 karakter olmalı, büyük harf, küçük harf, rakam ve özel karakter içermelidir."
            );
        }

        // Şifre hash'leme
        user.setPassword(passwordEncoder.encode(password));

        // Hesap aktif mi, e-posta doğrulaması bekleniyor
        user.setEnabled(false);

        // Cinsiyete göre profil resmi
        String gender = user.getGender();
        if ("MALE".equalsIgnoreCase(gender)) {
            user.setProfileImageUrl("/images/maleIcon.png");
        } else if ("FEMALE".equalsIgnoreCase(gender)) {
            user.setProfileImageUrl("/images/femaleIcon.png");
        } else {
            user.setProfileImageUrl("/images/defaultIcon.png");
        }

        // Verification token
        String token = java.util.UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // E-posta gönder
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());

        return ResponseEntity.ok("Kayıt başarılı. Lütfen e-postanızı kontrol edin.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException, java.io.IOException {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Doğrulama süresi geçmiş. Lütfen tekrar kayıt olun.");
        }

        String cleanToken = token.trim();
        Optional<User> userOpt = userRepository.findByVerificationToken(cleanToken);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Doğrulama süresi geçmiş. Lütfen tekrar kayıt olun.");
        }

        User user = userOpt.get();
        LocalDateTime tokenCreated = user.getVerificationTokenCreatedAt();
        if (tokenCreated == null) {
            return ResponseEntity.badRequest().body("Doğrulama süresi geçmiş. Lütfen tekrar kayıt olun.");
        }

        if (tokenCreated.plusMinutes(5).isBefore(LocalDateTime.now())) {
            userRepository.delete(user); // Süresi geçmiş kullanıcıyı sil
            return ResponseEntity.badRequest().body("Doğrulama süresi geçmiş. Lütfen tekrar kayıt olun.");
        }

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("Bu hesap zaten doğrulanmış.");
        }

        // Başarılı doğrulama
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenCreatedAt(null);
        userRepository.save(user);

        // Sadece başarılı ise login sayfasına yönlendir
        response.sendRedirect("https://taskify.rumer.tr/auth/login");
        return null; // response zaten yönlendirdiği için body döndürmeye gerek yok
    }


    // Kullanıcı giriş endpoint'i, başarılı olursa JWT token döner
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername())
            .map(existingUser -> {
                if (!existingUser.isEnabled()) {
                    return ResponseEntity.status(403).body("E-posta doğrulaması tamamlanmamış.");
                }

                try {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
                    );

                    String token = jwtUtils.generateJwtToken(existingUser.getUsername());

                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);

                    return ResponseEntity.ok(response);

                } catch (AuthenticationException e) {
                    return ResponseEntity.status(401).body("Hatalı kullanıcı adı veya şifre");
                }
            })
            .orElse(ResponseEntity.status(404).body("Kullanıcı bulunamadı."));
    }

    // Yeni doğrulama e-postası gönderme endpoint'i
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam("email") String email) {
        return userRepository.findByEmail(email)
            .map(user -> {
                if (user.isEnabled()) {
                    return ResponseEntity.badRequest().body("Bu hesap zaten doğrulanmış.");
                }

                // Yeni token oluştur
                String newToken = java.util.UUID.randomUUID().toString();
                user.setVerificationToken(newToken);
                user.setVerificationTokenCreatedAt(LocalDateTime.now());
                User savedUser = userRepository.save(user);

                // Yeni doğrulama e-postası gönder
                emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());

                return ResponseEntity.ok("Yeni doğrulama e-postası gönderildi. Lütfen e-postanızı kontrol edin.");
            })
            .orElse(ResponseEntity.badRequest().body("Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userService.sendResetPasswordMail(request);
            return ResponseEntity.ok("Şifre sıfırlama maili gönderildi.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
     
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
