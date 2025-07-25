package com.rumer.taskify.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import jakarta.persistence.Table;


@Data
@Entity
@Table(name = "\"user\"") // Çift tırnak PostgreSQL için zorunlu
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String fullName;
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
