package org.example.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Installation details from GitHub webhook payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Installation {

    /**
     * Installation ID (used for authentication)
     */
    private Long id;
}

