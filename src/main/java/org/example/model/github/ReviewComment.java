package org.example.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a review comment on a pull request.
 * Response from GitHub API when creating or fetching review comments.
 */
@Data
public class ReviewComment {

    /**
     * Unique identifier for the comment.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Node ID for GraphQL API.
     */
    @JsonProperty("node_id")
    private String nodeId;

    /**
     * The text of the comment.
     */
    @JsonProperty("body")
    private String body;

    /**
     * The relative path to the file that the comment applies to.
     */
    @JsonProperty("path")
    private String path;

    /**
     * The line of the blob in the pull request diff that the comment applies to.
     */
    @JsonProperty("line")
    private Integer line;

    /**
     * The side of the diff that the comment applies to (LEFT or RIGHT).
     */
    @JsonProperty("side")
    private String side;

    /**
     * The SHA of the commit the comment was made on.
     */
    @JsonProperty("commit_id")
    private String commitId;

    /**
     * The SHA of the original commit the comment was made on.
     */
    @JsonProperty("original_commit_id")
    private String originalCommitId;

    /**
     * The position in the diff that the comment applies to.
     */
    @JsonProperty("position")
    private Integer position;

    /**
     * The original position in the diff that the comment applies to.
     */
    @JsonProperty("original_position")
    private Integer originalPosition;

    /**
     * URL to the comment.
     */
    @JsonProperty("html_url")
    private String htmlUrl;

    /**
     * API URL for the comment.
     */
    @JsonProperty("url")
    private String url;

    /**
     * Timestamp when the comment was created.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * Timestamp when the comment was last updated.
     */
    @JsonProperty("updated_at")
    private String updatedAt;
}

