package com.rumer.taskify.service;

import com.rumer.taskify.model.user;
import com.rumer.taskify.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // constructor injection için
public class UserDetailsServiceImp implements UserDetailsService {

    private final userRepository userRepository;

    // Spring Security bu metodu otomatik çağırır: Login işlemi burada başlar
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Veritabanında kullanıcıyı ara
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }
}
