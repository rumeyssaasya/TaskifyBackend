package com.rumer.taskify.scheduler;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.UserRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Component
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    public UserCleanupScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Her dakika çalışacak
    @Scheduled(fixedRate = 60000)
    public void removeUnverifiedUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isEnabled() && user.getVerificationTokenCreatedAt() != null) {
                Duration duration = Duration.between(user.getVerificationTokenCreatedAt(), LocalDateTime.now());
                if (duration.toMinutes() >= 6) {
                    userRepository.delete(user);
                    System.out.println("6 dakikada doğrulanmamış kullanıcı silindi: " + user.getUsername());
                }
            }
        }
    }
}

