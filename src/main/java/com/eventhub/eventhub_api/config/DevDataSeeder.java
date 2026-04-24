package com.eventhub.eventhub_api.config;

import com.eventhub.eventhub_api.model.Role;
import com.eventhub.eventhub_api.model.User;
import com.eventhub.eventhub_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("admin@eventhub.com", "Ada",  "Admin", "admin123", Role.ADMIN);
        seed("user@eventhub.com",  "Ulla", "User",  "user123",  Role.USER);
    }

    private void seed(String email, String firstName, String lastName, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email)) return;
        User u = new User();
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setCreatedAt(LocalDateTime.now());
        userRepository.save(u);
    }
}
