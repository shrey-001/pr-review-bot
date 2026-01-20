# PR Review Bot

A production-ready GitHub App that automatically reviews pull requests, generates documentation or improvements, and commits changes directly to PR branches using GitHub APIs (no git clone required).

## Features

- ✅ **Stateless & Scalable**: No local git operations, fully API-based
- ✅ **Async Processing**: Returns 200 OK immediately, processes webhooks in background
- ✅ **Secure**: Webhook signature verification, GitHub App authentication
- ✅ **Production-Ready**: Health checks, monitoring, error handling
- ✅ **Smart Filtering**: Ignores forks, drafts, and bot-created PRs to prevent loops
- ✅ **Selective Processing**: Only processes added/modified files

## Architecture

```
┌─────────────┐
│   GitHub    │
│   Webhook   │
└──────┬──────┘
       │ POST /webhook/github
       ▼
┌─────────────────────────────────┐
│  GitHubWebhookController        │
│  - Verify signature             │
│  - Return 200 OK immediately    │
│  - Trigger async processing     │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│  PullRequestProcessor (Async)   │
│  - Fetch PR files               │
│  - Generate improvements        │
│  - Commit changes via Git API   │
└──────┬──────────────────────────┘
       │
       ├──► GitHubApiClient
       │    - Get PR files
       │    - Get file contents
       │
       └──► GitDataService
            - Create blobs
            - Create tree
            - Create commit
            - Update branch ref
```

## Project Structure

```
src/main/java/org/example/
├── auth/
│   ├── GitHubAppJwtService.java          # JWT generation for GitHub App
│   └── InstallationTokenService.java     # Installation token management
├── config/
│   ├── AsyncConfig.java                  # Async processing configuration
│   ├── GitHubAppConfig.java              # GitHub App settings
│   └── WebClientConfig.java              # HTTP client configuration
├── exception/
│   └── GlobalExceptionHandler.java       # Global error handling
├── git/
│   ├── GitDataService.java               # GitHub Git Data API client
│   └── GitHubApiClient.java              # GitHub REST API client
├── model/
│   ├── github/                           # GitHub API response models
│   └── webhook/                          # Webhook payload models
├── pr/
│   └── PullRequestProcessor.java         # Core PR processing logic
├── util/
│   └── SignatureVerifier.java            # Webhook signature verification
├── webhook/
│   └── GitHubWebhookController.java      # Webhook endpoint
└── Main.java                             # Application entry point
```

## How It Works

### 1. Webhook Handling

When a PR is opened or synchronized:

1. GitHub sends a webhook to `POST /webhook/github`
2. Controller verifies the signature using `X-Hub-Signature-256`
3. Controller returns 200 OK immediately
4. Processing happens asynchronously in background

### 2. PR Filtering

The bot ignores:
- PRs from forks (GitHub security restriction)
- Draft PRs
- PRs where the last commit author is the bot (prevents infinite loops)

### 3. File Processing

For each changed file:
1. Fetch file content from PR head branch
2. Generate improved content using `generateUpdatedContent()`
3. Create blob for updated content
4. Track changes for commit

### 4. Committing Changes

Using GitHub Git Data API (no git clone):

1. **Get branch reference**: `GET /repos/{owner}/{repo}/git/ref/heads/{branch}`
2. **Get current commit**: `GET /repos/{owner}/{repo}/git/commits/{sha}`
3. **Create blobs**: `POST /repos/{owner}/{repo}/git/blobs` (for each file)
4. **Create tree**: `POST /repos/{owner}/{repo}/git/trees` (with updated files)
5. **Create commit**: `POST /repos/{owner}/{repo}/git/commits`
6. **Update branch**: `PATCH /repos/{owner}/{repo}/git/refs/heads/{branch}`

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- GitHub App credentials

### 1. Build

```bash
mvn clean package
```

### 2. Configure

Set environment variables:

```bash
export GITHUB_APP_ID=your-app-id
export GITHUB_INSTALLATION_ID=your-installation-id
export GITHUB_WEBHOOK_SECRET=your-webhook-secret
export GITHUB_PRIVATE_KEY="$(cat path/to/private-key.pem)"
```

### 3. Run

```bash
java -jar target/pr-review-bot-1.0-SNAPSHOT.jar
```

Or with Maven:

```bash
mvn spring-boot:run
```

### 4. Test

```bash
# Health check
curl http://localhost:3000/actuator/health

# Webhook info
curl http://localhost:3000/webhook/info
```

## Customization

### Implement Your Review Logic

Edit `PullRequestProcessor.generateUpdatedContent()`:

```java
private String generateUpdatedContent(String originalContent, String path) {
    // TODO: Implement your custom logic here

    // Example: Add documentation
    if (path.endsWith(".java")) {
        return addJavadoc(originalContent);
    }

    // Example: Fix code style
    if (path.endsWith(".py")) {
        return formatPythonCode(originalContent);
    }

    // Example: Generate README
    if (path.equals("README.md")) {
        return enhanceReadme(originalContent);
    }

    return null; // No changes
}
```

## API Endpoints

### Webhook Endpoint

```
POST /webhook/github
Headers:
  - X-Hub-Signature-256: sha256=<signature>
  - X-GitHub-Event: pull_request
Body: GitHub webhook payload
```

### Health & Monitoring

```
GET /actuator/health       # Health check
GET /actuator/metrics      # Application metrics
GET /webhook/info          # Webhook configuration
```

## Configuration

All configuration is done via environment variables or `application.properties`:

| Variable | Description | Required |
|----------|-------------|----------|
| `GITHUB_APP_ID` | GitHub App ID | Yes |
| `GITHUB_INSTALLATION_ID` | Installation ID | Yes |
| `GITHUB_WEBHOOK_SECRET` | Webhook secret | Yes |
| `GITHUB_PRIVATE_KEY` | Private key (PEM format) | Yes |
| `SERVER_PORT` | Server port (default: 3000) | No |

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed deployment instructions including:
- Docker deployment
- Kubernetes deployment
- Security best practices
- Monitoring setup

## Development

### Run Tests

```bash
mvn test
```

### Local Webhook Testing

Use ngrok to expose your local server:

```bash
ngrok http 3000
```

Update GitHub App webhook URL to: `https://your-ngrok-url.ngrok.io/webhook/github`

## Security

- ✅ Webhook signature verification (HMAC-SHA256)
- ✅ GitHub App authentication (JWT + Installation tokens)
- ✅ No sensitive data in logs
- ✅ Secure token caching with expiration
- ✅ Input validation and sanitization

## License

MIT
A GitHub App that automatically reviews pull requests, generates documentation, and pushes improvements directly to the PR branch.
