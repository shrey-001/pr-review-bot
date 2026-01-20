package org.example.client;

import org.example.client.request.CreateReviewCommentRequest;
import org.example.model.github.FileContent;
import org.example.model.github.PullRequestFile;
import org.example.model.github.ReviewComment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for GitHub REST API.
 * Provides declarative REST client for fetching PR files and file contents.
 * 
 * Authentication is handled by FeignClientInterceptor which adds the
 * Authorization header with the installation access token.
 */
@FeignClient(
    name = "github-api",
    url = "${github.app.api-base-url}",
    configuration = FeignClientConfig.class
)
public interface GitHubApiClient {

    /**
     * Fetches the list of files changed in a pull request.
     * GET /repos/{owner}/{repo}/pulls/{pull_number}/files
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param pullNumber PR number
     * @return List of changed files
     */
    @GetMapping("/repos/{owner}/{repo}/pulls/{pullNumber}/files")
    List<PullRequestFile> getPullRequestFiles(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("pullNumber") int pullNumber
    );

    /**
     * Fetches the content of a file from a specific branch.
     * GET /repos/{owner}/{repo}/contents/{path}?ref={branch}
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path
     * @param ref Branch name or commit SHA
     * @return File content (Base64 encoded)
     */
    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    FileContent getFileContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("path") String path,
            @RequestParam("ref") String ref
    );

    /**
     * Creates a review comment on a pull request.
     * Adds an inline comment on a specific line of code in the PR diff.
     * POST /repos/{owner}/{repo}/pulls/{pull_number}/comments
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param pullNumber PR number
     * @param request Review comment details (body, path, line, commit_id, etc.)
     * @return Created review comment
     */
    @PostMapping("/repos/{owner}/{repo}/pulls/{pullNumber}/comments")
    ReviewComment createReviewComment(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("pullNumber") int pullNumber,
            @RequestBody CreateReviewCommentRequest request
    );
}

