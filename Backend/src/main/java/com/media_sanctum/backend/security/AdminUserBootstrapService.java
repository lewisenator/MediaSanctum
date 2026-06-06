package com.media_sanctum.backend.security;

import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.model.UpdateUserModel;
import com.media_sanctum.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminUserBootstrapService {

    private final String adminEmail;
    private final String adminPassword;

    private final UserService userService;

    public AdminUserBootstrapService(
            @Value("${ADMIN_EMAIL}") String adminEmail,
            @Value("${ADMIN_PASSWORD}") String adminPassword,
            UserService userService
    ) {
        this.adminEmail = adminEmail.strip();
        this.adminPassword = adminPassword.strip();
        this.userService = userService;
    }

    public void bootstrap() {
        var maybeAdmin = userService.getUserModelByEmail(adminEmail);
        if (maybeAdmin.isPresent()) {
            // Ensure existing admin user has correct password
            var adminUser = maybeAdmin.get();

            var adminUserUpdate = UpdateUserModel.builder()
                    .id(adminUser.getId())
                    .password(adminPassword)
                    .build();

            userService.updateUser(adminUserUpdate);
            log.info("Admin user {} password refreshed", adminEmail);
        } else {
            var adminUser = new CreateUserModel();
            adminUser.setEmail(adminEmail);
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPassword(adminPassword);

            userService.createUser(adminUser);
            log.info("Admin user {} created", adminEmail);
        }
    }
}
