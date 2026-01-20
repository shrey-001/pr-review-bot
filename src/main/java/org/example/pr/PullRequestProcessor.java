package org.example.pr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.github.PullRequestFile;
import org.example.model.webhook.WebhookPayload;
import org.example.pr.filter.PullRequestFilters;
import org.example.service.GitHubService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Processes pull request webhook events.
 * Fetches changed files and applies filtering logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PullRequestProcessor {

    private final PullRequestFilters filters;
    private final GitHubService githubService;


    /**
     * Processes a pull request webhook event asynchronously.
     * Fetches and logs changed files for the pull request.
     *
     * @param payload Webhook payload
     */
    @Async("webhookExecutor")
    public void processPullRequest(WebhookPayload payload) {
        int prNumber = payload.getPullRequest().getNumber();
        String owner = payload.getRepository().getOwner().getLogin();
        String repo = payload.getRepository().getName();

        log.info("Processing PR #{} in repository {}/{}", prNumber, owner, repo);

        try {
            // Step 1: Filter PR
            if (!filters.shouldProcessPullRequest(payload)) {
                String reason = filters.getFilterReason(payload);
                log.info("Skipping PR #{}: {}", prNumber, reason);
                return;
            }

            // Step 2: Fetch changed files
            List<PullRequestFile> changedFiles = fetchChangedFiles(owner, repo, prNumber);
            if (changedFiles.isEmpty()) {
                log.info("No files changed in PR #{}", prNumber);
                return;
            }

            log.info("Changed files: {}", changedFiles);
            log.info("Successfully processed PR #{} with {} changed files",
                    prNumber, changedFiles.size());

        } catch (Exception e) {
            log.error("Failed to process pull request #{}", prNumber, e);
        }
    }

    /**
     * Fetches changed files from a pull request.
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber PR number
     * @return List of changed files
     */
    private List<PullRequestFile> fetchChangedFiles(String owner, String repo, int prNumber) {
        List<PullRequestFile> files = githubService.getPullRequestFiles(owner, repo, prNumber);
        return files != null ? files : List.of();
    }
}

