package org.example.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.GitHubAppConfig;
import org.example.model.webhook.WebhookPayload;
import org.example.pr.PullRequestProcessor;
import org.example.util.SignatureVerifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Controller for handling GitHub webhook events.
 * Endpoint: POST /webhook/github
 * 
 * Handles pull_request events with actions: opened, synchronize
 * Returns 200 OK immediately and processes webhooks asynchronously.
 */
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class GitHubWebhookController {

    private final PullRequestProcessor pullRequestProcessor;
    private final SignatureVerifier signatureVerifier;
    private final GitHubAppConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Actions we want to process
    private static final Set<String> SUPPORTED_ACTIONS = Set.of("opened", "synchronize");

    /**
     * Handles GitHub webhook events.
     * POST /webhook/github
     *
     * @param signature Webhook signature from X-Hub-Signature-256 header
     * @param event Event type from X-GitHub-Event header
     * @param payload Raw webhook payload
     * @return 200 OK response
     */
    @PostMapping("/github")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestBody String payload) {

        log.info("Received GitHub webhook event: {}", event);

        try {
            // Step 1: Verify webhook signature
            if (!signatureVerifier.verifySignature(payload, signature, config.getWebhookSecret())) {
                log.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid signature"));
            }

            // Step 2: Check if this is a pull_request event
            if (!"pull_request".equals(event)) {
                log.debug("Ignoring non-pull_request event: {}", event);
                return ResponseEntity.ok(Map.of(
                        "status", "ignored",
                        "message", "Not a pull_request event"
                ));
            }

            // Step 3: Parse webhook payload
            WebhookPayload webhookPayload = objectMapper.readValue(payload, WebhookPayload.class);

            // Step 4: Check if action is supported
            String action = webhookPayload.getAction();
            if (!SUPPORTED_ACTIONS.contains(action)) {
                log.debug("Ignoring pull_request action: {}", action);
                return ResponseEntity.ok(Map.of(
                        "status", "ignored",
                        "message", "Action not supported: " + action
                ));
            }

            // Step 5: Process pull request asynchronously
            // This returns immediately while processing happens in background
            pullRequestProcessor.processPullRequest(webhookPayload);

            log.info("Accepted pull_request webhook for PR #{} (action: {})", 
                    webhookPayload.getPullRequest().getNumber(), action);

            // Return 200 OK immediately
            return ResponseEntity.ok(Map.of(
                    "status", "accepted",
                    "message", "Webhook received and processing started",
                    "pr_number", String.valueOf(webhookPayload.getPullRequest().getNumber()),
                    "action", action
            ));

        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to process webhook: " + e.getMessage()
                    ));
        }
    }

    /**
     * Health check endpoint for the webhook service.
     * GET /webhook/health
     *
     * @return 200 OK with service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "pr-review-bot",
                "endpoint", "/webhook/github"
        ));
    }

    /**
     * Info endpoint to get webhook configuration details.
     * GET /webhook/info
     *
     * @return Webhook configuration information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "endpoint", "/webhook/github",
                "method", "POST",
                "supported_events", "pull_request",
                "supported_actions", SUPPORTED_ACTIONS,
                "description", "GitHub App webhook endpoint for automated PR reviews"
        ));
    }
}

