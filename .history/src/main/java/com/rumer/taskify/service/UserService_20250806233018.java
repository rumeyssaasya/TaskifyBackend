package com.rumer.taskify.service;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password, String email, String fullName, String gender) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setGender(gender);

        // Profil fotoğrafı atanmadıysa gender'a göre varsayılanı ata
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            switch (gender.toUpperCase()) {
                case "MALE":
                    user.setProfileImageUrl("https://yourdomain.com/images/default_male.png");
                    break;
                case "FEMALE":
                    user.setProfileImageUrl("https://yourdomain.com/images/default_female.png");
                    break;
                default:
                    user.setProfileImageUrl("https://yourdomain.com/images/default_generic.png");
                    break;
            }
        }

        return userRepository.save(user);
    }
}

