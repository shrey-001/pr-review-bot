package org.example.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a file changed in a pull request.
 * Response from GET /repos/{owner}/{repo}/pulls/{pull_number}/files
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestFile {

    /**
     * File path (e.g., "src/main/java/Example.java")
     */
    private String filename;

    /**
     * File status: "added", "modified", "removed", "renamed"
     */
    private String status;

    /**
     * Number of additions
     */
    private Integer additions;

    /**
     * Number of deletions
     */
    private Integer deletions;

    /**
     * Number of changes
     */
    private Integer changes;

    /**
     * Raw URL to download the file
     */
    @JsonProperty("raw_url")
    private String rawUrl;

    /**
     * Previous filename (for renamed files)
     */
    @JsonProperty("previous_filename")
    private String previousFilename;

    /**
     * Patch/diff content
     */
    private String patch;
}

