package org.example.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Configuration properties for GitHub App authentication and settings.
 * These values should be loaded from environment variables or application.properties.
 */
@Configuration
@ConfigurationProperties(prefix = "github.app")
@Data
@Validated
@Slf4j
public class GitHubAppConfig {

    /**
     * GitHub App ID (found in GitHub App settings)
     */
    @NotNull(message = "GitHub App ID is required")
    private Long appId;

    /**
     * GitHub App Installation ID (obtained when app is installed on a repository)
     */
    @NotNull(message = "GitHub App Installation ID is required")
    private Long installationId;

    /**
     * GitHub App Private Key in PEM format (direct content)
     * Can be set directly or loaded from privateKeyPath
     */
    private String privateKey;

    /**
     * Path to GitHub App Private Key file (alternative to privateKey)
     * Example: classpath:github-app-private-key.pem or file:/path/to/key.pem
     */
    private Resource privateKeyPath;

    /**
     * Webhook secret for signature verification
     */
    @NotBlank(message = "Webhook secret is required")
    private String webhookSecret;

    /**
     * GitHub API base URL (default: https://api.github.com)
     */
    private String apiBaseUrl = "https://api.github.com";

    /**
     * Bot name used to identify commits made by this app
     */
    private String botName = "pr-review-bot[bot]";

    /**
     * JWT expiration time in minutes (GitHub recommends max 10 minutes)
     */
    private int jwtExpirationMinutes = 10;

    /**
     * Installation token cache duration in minutes (tokens are valid for 1 hour)
     */
    private int tokenCacheDurationMinutes = 50;

    /**
     * Load private key from file if privateKeyPath is specified and privateKey is not set.
     */
    @PostConstruct
    public void loadPrivateKey() throws IOException {
        if ((privateKey == null || privateKey.isBlank()) && privateKeyPath != null) {
            log.info("Loading private key from: {}", privateKeyPath);
            privateKey = privateKeyPath.getContentAsString(StandardCharsets.UTF_8);
            log.info("Private key loaded successfully from file");
        } else if (privateKey != null && !privateKey.isBlank()) {
            log.info("Using private key from direct configuration");
        } else {
            throw new IllegalStateException("Either github.app.private-key or github.app.private-key-path must be configured");
        }
    }
}

