package com.rumer.taskify.service;

import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByVerificationToken(String token) {
        return userRepository.findByVerificationToken(token);
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
                    user.setProfileImageUrl("http://localhost:8080/images/maleIcon.png");
                    break;
                case "FEMALE":
                    user.setProfileImageUrl("http://localhost:8080/images/femaleIcon.png");
                    break;
                default:
                    user.setProfileImageUrl("http://localhost:8080/images/defaultIcon.png");
                    break;
            }
        }

        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false; // Eski şifre yanlış
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public User updateProfile(User user, String fullName, String profileImageUrl) {
        user.setFullName(fullName);
        if (profileImageUrl != null) {
            user.setProfileImageUrl(profileImageUrl);
        }
        return userRepository.save(user);
    }
}
