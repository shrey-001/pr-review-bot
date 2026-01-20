package org.example.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.config.GitHubAppConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

/**
 * Service for generating JWT tokens for GitHub App authentication.
 * GitHub Apps use JWT tokens to authenticate as the app itself.
 * The JWT is then exchanged for an installation access token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubAppJwtService {

    private final GitHubAppConfig config;
    private PrivateKey privateKey;

    /**
     * Generates a JWT token for GitHub App authentication.
     * The token is signed with RS256 using the app's private key.
     *
     * @return JWT token string
     */
    public String generateJwt() {
        try {
            if (privateKey == null) {
                privateKey = parsePrivateKey(config.getPrivateKey());
            }
            log.info("private key {}", privateKey);

            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(config.getJwtExpirationMinutes() * 60L);

            // Build JWT according to GitHub's requirements
            // https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/generating-a-json-web-token-jwt-for-a-github-app
            String jwt = Jwts.builder()
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .setIssuer(config.getAppId().toString())
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();

            log.debug("Generated JWT token for GitHub App ID: {}", config.getAppId());
            return jwt;

        } catch (Exception e) {
            log.error("Failed to generate JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Parses a PEM-encoded private key string into a PrivateKey object.
     * Expects PKCS#1 format (-----BEGIN RSA PRIVATE KEY-----) as provided by GitHub.
     *
     * @param pemKey PEM-encoded private key string
     * @return PrivateKey object
     * @throws IOException if parsing fails
     */
    private PrivateKey parsePrivateKey(String pemKey) throws IOException {
        // Normalize the PEM key (handle escaped newlines from environment variables)
        String normalizedKey = pemKey.replace("\\n", "\n");

        try (PEMParser pemParser = new PEMParser(new StringReader(normalizedKey))) {
            PEMKeyPair keyPair = (PEMKeyPair) pemParser.readObject();

            if (keyPair == null) {
                throw new IllegalArgumentException("Failed to parse private key. Ensure it's in PKCS#1 format (-----BEGIN RSA PRIVATE KEY-----)");
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPrivateKey(keyPair.getPrivateKeyInfo());
        }
    }
}

