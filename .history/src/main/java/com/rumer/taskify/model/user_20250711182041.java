package com.rumer.taskify.model;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class user {
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
