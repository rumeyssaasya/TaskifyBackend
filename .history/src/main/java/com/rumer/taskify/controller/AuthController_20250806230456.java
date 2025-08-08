package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.security.JwtUtils;
import com.rumer.taskify.service.EmailService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
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

        String gender = user.getGender(); // Eğer entity'de yoksa, DTO ile yap
        if ("MALE".equalsIgnoreCase(gender)) {
            user.setProfileImageUrl("/images/maleIcon.png");
        } else if ("FEMALE".equalsIgnoreCase(gender)) {
            user.setProfileImageUrl("/images/femaleIcon.png");
        } else {
        user.setProfileImageUrl("/images/defaultIcon.png"); // Genel default, boş bırakma
}

        String token = java.util.UUID.randomUUID().toString();  // Doğrulama token'ı üret
        user.setVerificationToken(token);
        
        user.setVerificationTokenCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());  // Email gönder

        return ResponseEntity.ok("Kayıt başarılı. Lütfen e-postanızı kontrol edin.");
    }
    // E-posta doğrulama endpoint'i
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        // Token parametresinin boş olup olmadığını kontrol et
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Doğrulama token'ı gereklidir.");
        }

        // Token'ı temizle (başındaki ve sonundaki boşlukları kaldır)
        String cleanToken = token.trim();

        Optional<User> userOpt = userRepository.findByVerificationToken(cleanToken);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Geçersiz doğrulama token'ı.");
        }
        
        User user = userOpt.get();
        
        // Token süresini kontrol et (5 dakika)
        LocalDateTime tokenCreated = user.getVerificationTokenCreatedAt();
        if (tokenCreated == null) {
            return ResponseEntity.badRequest().body("Token oluşturma tarihi bulunamadı.");
        }
        
        // 5 dakika süre kontrolü
        LocalDateTime expiryTime = tokenCreated.plusMinutes(5);
        LocalDateTime now = LocalDateTime.now();
        
        if (expiryTime.isBefore(now)) {
            return ResponseEntity.badRequest().body("Token süresi geçmiş. Lütfen yeni bir doğrulama e-postası talep edin.");
        }

        // Kullanıcı zaten doğrulanmış mı kontrol et
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("Bu hesap zaten doğrulanmış.");
        }
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenCreatedAt(null);
        userRepository.save(user);

        return ResponseEntity.ok("E-posta doğrulandı. Giriş yapabilirsiniz.");
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

}
