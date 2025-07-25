package com.rumer.taskify.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Collection;
import java.util.Collections;

@Entity
@Data // Getter, Setter, toString otomatik oluşturur
@NoArgsConstructor
@AllArgsConstructor
@Builder // Builder pattern'i destekler

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Girişte kullanılacak
    private String password; // BCrypt ile şifrelenmiş hali

    // Spring Security için gerekli — basit projede rollerle uğraşmayacağız
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Herkese aynı yetki
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesap süresi geçmemiş
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesap kilitli değil
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Şifre geçerliliği devam ediyor
    }

    @Override
    public boolean isEnabled() {
        return true; // Kullanıcı aktif
    }
}
