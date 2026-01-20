package org.example.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Root webhook payload received from GitHub.
 * Contains the action and pull request details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPayload {

    /**
     * The action that was performed (e.g., "opened", "synchronize", "closed")
     */
    private String action;

    /**
     * Pull request details
     */
    @JsonProperty("pull_request")
    private PullRequest pullRequest;

    /**
     * Repository where the event occurred
     */
    private Repository repository;

    /**
     * Installation details (contains installation ID)
     */
    private Installation installation;

    /**
     * User who triggered the event
     */
    private User sender;
}

