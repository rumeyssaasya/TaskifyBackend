package com.rumer.taskify.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Spring tarafından otomatik olarak bean olarak yönetilsin
public class JwtUtils {

    // Token'ı imzalamak için kullanılacak gizli anahtar
    // Gerçek projelerde bu key environment (env) dosyasından alınır
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HMAC-SHA256 algoritması

    // Token geçerlilik süresi (milisaniye cinsinden): 24 saat
    private final long jwtExpirationMs = 24 * 60 * 60 * 1000;

    // ✅ 1. Token üretme
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Token'ın konusu: genellikle username
                .setIssuedAt(new Date()) // Oluşturulma tarihi
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Süresi
                .signWith(key) // İmzalama anahtarı
                .compact(); // Token'ı oluştur
    }

    // ✅ 2. Token'dan username'i çekme
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Token'ı doğrulamak için aynı anahtar
                .build()
                .parseClaimsJws(token) // Token'ı çöz
                .getBody()
                .getSubject(); // İçindeki username'i al
    }

    // ✅ 3. Token geçerli mi diye kontrol etme
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken); // Hatalıysa exception fırlatır
            return true;
        } catch (JwtException e) {
            // Token hatalıysa (bozuk, süresi geçmiş, imza uyuşmuyor vs.) false dön
            return false;
        }
    }
}
