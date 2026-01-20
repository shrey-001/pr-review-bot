package org.example.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for creating a review comment on a pull request.
 * Used to add inline comments on specific lines of code.
 * 
 * GitHub API: POST /repos/{owner}/{repo}/pulls/{pull_number}/comments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewCommentRequest {

    /**
     * The text of the review comment.
     */
    @JsonProperty("body")
    private String body;

    /**
     * The SHA of the commit needing a comment.
     * Not using the latest commit SHA may render your comment outdated if a subsequent commit modifies the line you specify.
     */
    @JsonProperty("commit_id")
    private String commitId;

    /**
     * The relative path to the file that necessitates a comment.
     */
    @JsonProperty("path")
    private String path;

    /**
     * The line of the blob in the pull request diff that the comment applies to.
     * For a multi-line comment, the last line of the range that your comment applies to.
     */
    @JsonProperty("line")
    private Integer line;

    /**
     * The side of the diff that the comment applies to.
     * Can be LEFT or RIGHT. Use LEFT for deletions that appear in red.
     * Use RIGHT for additions that appear in green or unchanged lines that appear in white and are shown for context.
     * Default: RIGHT
     */
    @JsonProperty("side")
    private String side;

    /**
     * The starting line of the range for a multi-line comment.
     * Required when using multi-line comments unless using in_reply_to.
     */
    @JsonProperty("start_line")
    private Integer startLine;

    /**
     * The starting side of the range for a multi-line comment.
     * Can be LEFT or RIGHT. To use LEFT, you must also specify start_line.
     * Default: RIGHT
     */
    @JsonProperty("start_side")
    private String startSide;
}

