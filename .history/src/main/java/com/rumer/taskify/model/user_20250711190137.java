package com.rumer.taskify.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Roller yoksa boş liste
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesap geçerlilik durumu
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesap kilit durumu
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Şifre geçerlilik durumu
    }

    @Override
    public boolean isEnabled() {
        return true; // Kullanıcı aktifliği
    }

    @Override
    public String getUsername() {
        return username; // username döner
    }

    @Override
    public String getPassword() {
        return password; // password döner
    }
}
