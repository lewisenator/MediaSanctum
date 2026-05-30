package com.media_sanctum.backend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_sanctum.backend.client.hardcover.HardcoverClient;
import com.media_sanctum.backend.config.MediaSanctumHardcoverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public HardcoverClient hardCoverClient(
            MediaSanctumHardcoverConfig hardCoverConfig,
            ObjectMapper objectMapper
    ) {
        var restClient = RestClient.builder()
                .baseUrl(hardCoverConfig.endpoint())
                .build();

        return new HardcoverClient(
                hardCoverConfig.apiKey(),
                restClient,
                objectMapper
        );
    }

}
