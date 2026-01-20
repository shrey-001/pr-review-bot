package org.example.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents file content from GitHub API.
 * Response from GET /repos/{owner}/{repo}/contents/{path}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileContent {

    /**
     * File name
     */
    private String name;

    /**
     * File path
     */
    private String path;

    /**
     * File SHA
     */
    private String sha;

    /**
     * File size in bytes
     */
    private Long size;

    /**
     * Content encoding (usually "base64")
     */
    private String encoding;

    /**
     * Base64-encoded file content
     */
    private String content;

    /**
     * Type (file, dir, symlink, submodule)
     */
    private String type;
}

