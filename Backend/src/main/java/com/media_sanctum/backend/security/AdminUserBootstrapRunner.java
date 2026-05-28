package com.media_sanctum.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminUserBootstrapRunner implements CommandLineRunner {

    private final AdminUserBootstrapService bootstrapService;

    public AdminUserBootstrapRunner(AdminUserBootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @Override
    public void run(String... args) {
        bootstrapService.bootstrap();
    }
}
