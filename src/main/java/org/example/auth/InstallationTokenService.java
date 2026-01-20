package org.example.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.GitHubAuthClient;
import org.example.config.GitHubAppConfig;
import org.example.model.github.InstallationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Service for managing GitHub App installation access tokens.
 * Installation tokens are used to authenticate API requests on behalf of the app installation.
 * Tokens are valid for 1 hour and should be cached.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstallationTokenService {

    private final GitHubAppJwtService jwtService;
    private final GitHubAppConfig config;
    private final GitHubAuthClient authClient;

    // Cache for installation token
    private String cachedToken;
    private Instant tokenExpiration;

    /**
     * Gets a valid installation access token.
     * Returns cached token if still valid, otherwise requests a new one.
     *
     * @return Installation access token
     */
    public String getInstallationToken() {
        // Return cached token if still valid (with 5-minute buffer)
        if (cachedToken != null && tokenExpiration != null) {
            if (Instant.now().isBefore(tokenExpiration.minusSeconds(300))) {
                log.debug("Using cached installation token");
                return cachedToken;
            }
        }

        // Request new token
        log.info("Requesting new installation access token for installation ID: {}",
                config.getInstallationId());

        String jwt = jwtService.generateJwt();

        try {
            // Exchange JWT for installation access token
            // POST /app/installations/{installation_id}/access_tokens
            InstallationToken response = authClient.createInstallationToken(
                    "Bearer " + jwt,
                    "application/vnd.github+json",
                    "2022-11-28",
                    config.getInstallationId()
            );

            if (response == null || response.getToken() == null) {
                throw new RuntimeException("Failed to obtain installation token: empty response");
            }

            // Cache the token
            cachedToken = response.getToken();
            tokenExpiration = Instant.from(
                    DateTimeFormatter.ISO_INSTANT.parse(response.getExpiresAt())
            );

            log.info("Successfully obtained installation token, expires at: {}", 
                    response.getExpiresAt());
            
            return cachedToken;

        } catch (Exception e) {
            log.error("Failed to obtain installation access token", e);
            throw new RuntimeException("Failed to obtain installation access token", e);
        }
    }

    /**
     * Invalidates the cached token, forcing a refresh on next request.
     */
    public void invalidateToken() {
        log.info("Invalidating cached installation token");
        cachedToken = null;
        tokenExpiration = null;
    }
}

