package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.GitHubApiClient;
import org.example.model.github.FileContent;
import org.example.model.github.PullRequestFile;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

/**
 * Service layer for GitHub API operations.
 * Wraps Feign clients with business logic and error handling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final GitHubApiClient apiClient;

    /**
     * Fetches the list of files changed in a pull request.
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param pullNumber PR number
     * @return List of changed files
     */
    public List<PullRequestFile> getPullRequestFiles(String owner, String repo, int pullNumber) {
        log.info("Fetching files for PR #{} in {}/{}", pullNumber, owner, repo);

        try {
            List<PullRequestFile> files = apiClient.getPullRequestFiles(owner, repo, pullNumber);
            log.info("Found {} changed files in PR #{}", 
                    files != null ? files.size() : 0, pullNumber);
            return files;

        } catch (Exception e) {
            log.error("Failed to fetch PR files for PR #{} in {}/{}", pullNumber, owner, repo, e);
            throw new RuntimeException("Failed to fetch PR files", e);
        }
    }

    /**
     * Fetches the content of a file from a specific branch.
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path
     * @param branch Branch name
     * @return Decoded file content as string
     */
    public String getFileContent(String owner, String repo, String path, String branch) {
        log.debug("Fetching content for file: {} from branch: {} in {}/{}", 
                path, branch, owner, repo);

        try {
            FileContent content = apiClient.getFileContent(owner, repo, path, branch);
            String decodedContent = decodeContent(content.getContent());
            
            log.debug("Successfully fetched content for file: {} ({} bytes)", 
                    path, decodedContent.length());
            return decodedContent;

        } catch (Exception e) {
            log.error("Failed to fetch file content for: {} in {}/{}", path, owner, repo, e);
            throw new RuntimeException("Failed to fetch file content: " + path, e);
        }
    }

    /**
     * Decodes Base64-encoded file content from GitHub API.
     *
     * @param encodedContent Base64-encoded content
     * @return Decoded content as string
     */
    private String decodeContent(String encodedContent) {
        if (encodedContent == null) {
            return null;
        }
        
        // Remove whitespace and newlines that GitHub adds to Base64 content
        String cleaned = encodedContent.replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        return new String(decoded);
    }
}

