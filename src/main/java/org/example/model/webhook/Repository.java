package org.example.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Repository details from GitHub webhook payload.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

    /**
     * Repository ID
     */
    private Long id;

    /**
     * Repository name (e.g., "my-repo")
     */
    private String name;

    /**
     * Full repository name (e.g., "owner/my-repo")
     */
    @JsonProperty("full_name")
    private String fullName;

    /**
     * Repository owner
     */
    private User owner;

    /**
     * Whether the repository is private
     */
    @JsonProperty("private")
    private Boolean isPrivate;

    /**
     * Whether the repository is a fork
     */
    private Boolean fork;

    /**
     * Default branch name
     */
    @JsonProperty("default_branch")
    private String defaultBranch;
}

