package org.example.pr.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.GitHubAppConfig;
import org.example.model.github.PullRequestFile;
import org.example.model.webhook.WebhookPayload;
import org.springframework.stereotype.Component;

/**
 * Centralized filtering logic for pull requests and files.
 * 
 * SOLID Principles Applied:
 * - Single Responsibility: Only responsible for filtering decisions
 * - Open/Closed: Can extend filtering rules without breaking existing code
 * - Interface Segregation: Provides focused methods for different filtering needs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PullRequestFilters {

    private final GitHubAppConfig config;

    /**
     * Determines if a pull request should be processed.
     * 
     * Filters out:
     * - PRs from forks (GitHub security restriction)
     * - Draft PRs
     * - PRs where the bot is the last commit author (prevents infinite loops)
     *
     * @param payload Webhook payload
     * @return true if PR should be processed
     */
    public boolean shouldProcessPullRequest(WebhookPayload payload) {
        // Filter 1: Ignore PRs from forks
        if (isPullRequestFromFork(payload)) {
            log.info("Ignoring PR #{} from fork: {}", 
                    payload.getPullRequest().getNumber(),
                    payload.getPullRequest().getHead().getRepository().getFullName());
            return false;
        }

        // Filter 2: Ignore draft PRs
        if (Boolean.TRUE.equals(payload.getPullRequest().getDraft())) {
            log.info("Ignoring draft PR #{}", payload.getPullRequest().getNumber());
            return false;
        }

        // Filter 3: Prevent infinite loops - ignore PRs where bot is the last commit author
        if (isBotTheAuthor(payload)) {
            log.info("Ignoring PR #{} - last commit by bot (preventing infinite loop)", 
                    payload.getPullRequest().getNumber());
            return false;
        }

        return true;
    }

    /**
     * Determines if a file should be processed.
     * Only processes files with status "added" or "modified".
     *
     * @param file Pull request file
     * @return true if file should be processed
     */
    public boolean shouldProcessFile(PullRequestFile file) {
        String status = file.getStatus();
        boolean shouldProcess = "added".equals(status) || "modified".equals(status);
        
        if (!shouldProcess) {
            log.debug("Skipping file {} with status: {}", file.getFilename(), status);
        }
        
        return shouldProcess;
    }

    /**
     * Gets the reason why a PR was filtered out.
     *
     * @param payload Webhook payload
     * @return Reason for filtering, or null if should be processed
     */
    public String getFilterReason(WebhookPayload payload) {
        if (isPullRequestFromFork(payload)) {
            return "PR is from a fork";
        }

        if (Boolean.TRUE.equals(payload.getPullRequest().getDraft())) {
            return "PR is a draft";
        }

        if (isBotTheAuthor(payload)) {
            return "Last commit by bot - preventing infinite loop";
        }

        return null;
    }

    /**
     * Checks if the pull request is from a fork.
     */
    private boolean isPullRequestFromFork(WebhookPayload payload) {
        return payload.getPullRequest().getHead().getRepository() != null &&
               Boolean.TRUE.equals(payload.getPullRequest().getHead().getRepository().getFork());
    }

    /**
     * Checks if the bot is the author of the PR.
     */
    private boolean isBotTheAuthor(WebhookPayload payload) {
        String lastCommitAuthor = payload.getPullRequest().getUser().getLogin();
        String botName = config.getBotName() + "[bot]";
        return botName.equals(lastCommitAuthor);
    }
}

