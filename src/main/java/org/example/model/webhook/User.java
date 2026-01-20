package org.example.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * User details from GitHub webhook payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    /**
     * User ID
     */
    private Long id;

    /**
     * Username/login
     */
    private String login;

    /**
     * User type (e.g., "User", "Bot")
     */
    private String type;
}

