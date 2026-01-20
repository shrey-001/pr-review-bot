package org.example.client;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.example.auth.InstallationTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients.
 * Adds authentication headers and logging configuration.
 */
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final InstallationTokenService tokenService;

    /**
     * Request interceptor that adds GitHub authentication headers to all requests.
     * Automatically adds:
     * - Authorization: Bearer {installation_token}
     * - Accept: application/vnd.github+json
     * - X-GitHub-Api-Version: 2022-11-28
     */
    @Bean
    public RequestInterceptor githubAuthInterceptor() {
        return requestTemplate -> {
            // Add installation access token
            String token = tokenService.getInstallationToken();
            requestTemplate.header("Authorization", "Bearer " + token);
            
            // Add GitHub API headers
            requestTemplate.header("Accept", "application/vnd.github+json");
            requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
        };
    }

    /**
     * Configures Feign logging level.
     * FULL logs headers, body, and metadata for both request and response.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Change to FULL for detailed debugging
    }
}

