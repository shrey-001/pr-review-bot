package org.example.client;

import feign.Logger;
import feign.RequestInterceptor;
import org.example.model.github.InstallationToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for GitHub App authentication.
 * Used to exchange JWT for installation access token.
 *
 * Note: This client does NOT use FeignClientConfig to avoid circular dependency.
 * Authentication is handled manually via @RequestHeader.
 */
@FeignClient(
    name = "github-auth",
    url = "${github.app.api-base-url}",
    configuration = GitHubAuthClient.AuthClientConfig.class
)
public interface GitHubAuthClient {

    /**
     * Minimal configuration for auth client.
     * Does NOT add authentication interceptor to avoid circular dependency.
     * Provides only basic logging configuration.
     */
    class AuthClientConfig {

        /**
         * Empty request interceptor to override the global one.
         * This prevents FeignClientConfig from being applied.
         */
        @Bean
        public RequestInterceptor noOpInterceptor() {
            return requestTemplate -> {
                // No-op: headers are passed via @RequestHeader annotations
            };
        }

        /**
         * Basic logging configuration.
         */
        @Bean
        public Logger.Level feignLoggerLevel() {
            return Logger.Level.BASIC;
        }
    }

    /**
     * Exchanges JWT for installation access token.
     * POST /app/installations/{installation_id}/access_tokens
     *
     * @param authorization JWT token (format: "Bearer {jwt}")
     * @param accept GitHub API accept header
     * @param apiVersion GitHub API version
     * @param installationId Installation ID
     * @return Installation access token
     */
    @PostMapping("/app/installations/{installationId}/access_tokens")
    InstallationToken createInstallationToken(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Accept") String accept,
            @RequestHeader("X-GitHub-Api-Version") String apiVersion,
            @PathVariable("installationId") Long installationId
    );
}

