package com.rumer.taskify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.validation.constraints.Pattern;
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

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    @jakarta.validation.constraints.Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String password;

    @Column(nullable = false)
    private boolean enabled = false; // Doğrulama yapılana kadar false
    
    @Column
    private String verificationToken; // UUID token eklenecek

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
        return this.enabled; // Kullanıcı aktifliği
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
