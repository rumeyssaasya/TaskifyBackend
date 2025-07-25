package com.rumer.taskify.config;

import com.rumer.taskify.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration // Bu sınıf Spring konfigürasyon sınıfı olduğunu belirtir
@RequiredArgsConstructor // final alanlar için constructor otomatik oluşturur
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // JWT doğrulama filtresi, dependency injection ile alınır

    // Security filtre zincirini oluşturur ve yapılandırır
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF korumasını kapat (REST API için genelde kapalı olur)
            .cors().and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // /api/auth/** ile başlayan endpoint'lere herkes erişebilir (login, register)
                .anyRequest().authenticated() // Diğer tüm isteklere kimlik doğrulaması zorunlu
            )

            // Yetkisiz erişim durumunda 401 Unauthorized yanıtı gönderir
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Yetkisiz erişim"))
            )

            // JWT doğrulama filtresini UsernamePasswordAuthenticationFilter'dan önce çalıştırır
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Yapılandırmayı tamamla ve SecurityFilterChain döndür
    }

    // Şifreleri güvenli şekilde saklamak için BCryptPasswordEncoder bean'i oluşturulur
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager bean'i oluşturulur, kullanıcı doğrulama işlemlerinde kullanılır
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React frontend domainin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Eğer cookie veya Authorization header varsa true olmalı

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
