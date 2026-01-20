package org.example.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents an installation access token from GitHub.
 * Response from POST /app/installations/{installation_id}/access_tokens
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallationToken {

    /**
     * The installation access token
     */
    private String token;

    /**
     * Token expiration time (ISO 8601 format)
     */
    @JsonProperty("expires_at")
    private String expiresAt;
}

