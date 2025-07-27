package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.security.JwtUtils;
import com.rumer.taskify.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Kullanıcı kayıt endpoint'i
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Kullanıcı zaten mevcut!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Şifre hash'leme
        user.setEnabled(false);  // Hesabı aktif etme, email doğrulaması bekleniyor

        String token = java.util.UUID.randomUUID().toString();  // Doğrulama token'ı üret
        user.setVerificationToken(token);
        
        user.setVerificationTokenCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), token);  // Email gönder

        return ResponseEntity.ok("Kayıt başarılı. Lütfen e-postanızı kontrol edin.");
    }
    // E-posta doğrulama endpoint'i
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return userRepository.findByVerificationToken(token)
            .map(user -> {
                // Token süresini kontrol et (5 dakika)
                LocalDateTime tokenCreated = user.getVerificationTokenCreatedAt();
                if (tokenCreated == null || tokenCreated.plusMinutes(5).isBefore(LocalDateTime.now())) {
                    return ResponseEntity.badRequest().body("Geçersiz veya süresi geçmiş.");
                }

                user.setEnabled(true);
                user.setVerificationToken(null);
                user.setVerificationTokenCreatedAt(null);
                userRepository.save(user);

                return ResponseEntity.ok("E-posta doğrulandı. Giriş yapabilirsiniz.");
            })
            .orElse(ResponseEntity.badRequest().body("Geçersiz veya süresi geçmiş."));
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

                    String token = jwtUtils.generateJwtToken(user.getUsername());

                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);

                    return ResponseEntity.ok(response);

                } catch (AuthenticationException e) {
                    return ResponseEntity.status(401).body("Hatalı kullanıcı adı veya şifre");
                }
            })
            .orElse(ResponseEntity.status(404).body("Kullanıcı bulunamadı."));
    }
}
