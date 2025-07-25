package com.rumer.taskify.config;

// Gerekli Spring ve Security sınıflarını içe aktarıyoruz
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

@Configuration // Bu sınıfın bir konfigürasyon sınıfı olduğunu belirtir
public class SecurityConfig {

    // SecurityFilterChain: Tüm güvenlik kurallarını tanımladığımız yer
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF koruması form-based uygulamalar için, REST API'de genelde kapatılır

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // /api/auth ile başlayan endpoint’lere herkes erişebilir (login, register)
                .anyRequest().authenticated() // Geri kalan tüm endpoint’ler için authentication (JWT token) gerekir
            )

            // Eğer kullanıcı yetkisiz şekilde bir yere erişirse 401 hatası dönülür
            .exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Yetkisiz erişim")) // 401 mesajı
            );

        return http.build(); // Security zincirini tamamla
    }

    // Şifreleri güvenli şekilde saklamak için BCrypt algoritmasını kullanıyoruz
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager: Giriş yapan kullanıcıyı kontrol eden mekanizma
    // UserDetailsService ile çalışır (birazdan onu da yazacağız)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
