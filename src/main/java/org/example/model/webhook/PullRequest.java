package org.example.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Pull request details from GitHub webhook payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {

    /**
     * PR number
     */
    private Integer number;

    /**
     * PR title
     */
    private String title;

    /**
     * PR state (open, closed)
     */
    private String state;

    /**
     * Whether the PR is a draft
     */
    private Boolean draft;

    /**
     * Head branch (source branch of the PR)
     */
    private Branch head;

    /**
     * Base branch (target branch of the PR)
     */
    private Branch base;

    /**
     * User who created the PR
     */
    private User user;

    /**
     * Represents a branch in the pull request
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Branch {
        /**
         * Branch name (e.g., "feature/new-feature")
         */
        @JsonProperty("ref")
        private String ref;

        /**
         * Commit SHA
         */
        private String sha;

        /**
         * Repository details
         */
        @JsonProperty("repo")
        private Repository repository;
    }
}

