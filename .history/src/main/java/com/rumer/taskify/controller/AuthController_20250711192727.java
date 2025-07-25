package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Kullanıcı kayıt endpoint'i
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Kullanıcı zaten mevcut!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Şifreyi hash'le
        userRepository.save(user);
        return ResponseEntity.ok("Kayıt başarılı");
    }

    // Kullanıcı giriş endpoint'i, başarılı olursa JWT token döner
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            // AuthenticationManager ile kullanıcı doğrula
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Başarılı giriş sonrası JWT üret
            String token = jwtUtils.generateJwtToken(user.getUsername());

            // Token'ı response içinde gönder
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Hatalı kullanıcı adı veya şifre");
        }
    }
}
