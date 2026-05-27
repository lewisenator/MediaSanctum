package com.media_sanctum.backend.security;

import com.media_sanctum.backend.entity.User;
import com.media_sanctum.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminUserBootstrapRunner implements CommandLineRunner {

    private final String adminEmail;
    private final String adminPassword;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminUserBootstrapRunner(
            @Value("${ADMIN_EMAIL}") String adminEmail,
            @Value("${ADMIN_PASSWORD}") String adminPassword,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminEmail = adminEmail.strip();
        this.adminPassword = adminPassword.strip();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        var maybeAdmin = userRepository.findByEmail(adminEmail);
        if (maybeAdmin.isPresent()) {
            // Ensure existing admin user has correct password
            var adminUser = maybeAdmin.get();
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            userRepository.save(adminUser);
            log.info("Admin user {} password refreshed", adminEmail);
        } else {
            var adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setActive(true);
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            userRepository.save(adminUser);
            log.info("Admin user {} created", adminEmail);
        }
    }
}
