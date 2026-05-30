package com.media_sanctum.backend;

import com.media_sanctum.backend.config.MediaSanctumConfig;
import com.media_sanctum.backend.config.MediaSanctumHardcoverConfig;
import com.media_sanctum.backend.config.MediaSanctumJwtConfig;
import com.media_sanctum.backend.config.MediaSanctumLogConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {
        MediaSanctumConfig.class,
        MediaSanctumLogConfig.class,
        MediaSanctumJwtConfig.class,
        MediaSanctumHardcoverConfig.class,
})
public class BackendApplication {

    // Keep "public" for Spring Boot Gradle plugin
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
