package com.rumer.taskify.security;
import com.rumer.taskify.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull

import java.io.IOException;

@RequiredArgsConstructor // final alanlar için constructor otomatik oluşturur
@Component // Spring'e bu sınıfın bir bileşen olduğunu bildirir
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils; // JWT token'ı üretme/doğrulama yardımcı sınıfı
    private final UserDetailsServiceImpl userDetailsService; // Kullanıcıyı username ile bulur

    @Override
    protected void doFilterInternal(        
                            @NonNull HttpServletRequest request,
                            @NonNull HttpServletResponse response,
                            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Header'dan token'ı al
        final String jwt;
        final String username;

        // Header boşsa ya da "Bearer " ile başlamıyorsa token yok sayılır
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Diğer filtrelere geç
            return;
        }

        jwt = authHeader.substring(7); // "Bearer " kısmını at, sadece token'ı al

        // Token geçerli değilse ilerlemeden filtreyi sonlandır
        if (!jwtUtils.validateJwtToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        username = jwtUtils.getUsernameFromJwtToken(jwt); // Token'dan username çekilir

        // Eğer SecurityContext'te kullanıcı yoksa (ilk kez doğruluyorsa)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // DB'den kullanıcıyı getir

            // Kullanıcıyı Spring Security'ye tanıt
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, // kimlik bilgisi
                            null, // credentials (şifre artık gerekmez)
                            userDetails.getAuthorities() // yetkiler (roller)
                    );

            // İsteğe ait detayları (IP vs.) auth objesine ekle
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Spring Security'nin güvenlik bağlamına kullanıcıyı yerleştir
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response); // İstek akışına devam et
    }
}
