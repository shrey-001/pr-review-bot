package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for verifying GitHub webhook signatures.
 * GitHub signs webhook payloads using HMAC-SHA256 with the webhook secret.
 */
@Component
@Slf4j
public class SignatureVerifier {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SIGNATURE_PREFIX = "sha256=";

    /**
     * Verifies the GitHub webhook signature.
     *
     * @param payload The raw webhook payload (request body)
     * @param signature The signature from X-Hub-Signature-256 header
     * @param secret The webhook secret configured in GitHub App settings
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String payload, String signature, String secret) {
        if (payload == null || signature == null || secret == null) {
            log.warn("Missing required parameters for signature verification");
            return false;
        }

        if (!signature.startsWith(SIGNATURE_PREFIX)) {
            log.warn("Invalid signature format. Expected format: sha256=<hash>");
            return false;
        }

        try {
            // Remove the "sha256=" prefix
            String expectedSignature = signature.substring(SIGNATURE_PREFIX.length());

            // Compute HMAC-SHA256 signature
            String computedSignature = computeHmacSha256(payload, secret);

            // Use constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    computedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    /**
     * Computes HMAC-SHA256 signature for the given payload and secret.
     *
     * @param payload The payload to sign
     * @param secret The secret key
     * @return Hex-encoded signature
     */
    private String computeHmacSha256(String payload, String secret) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA256
        );
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * Converts byte array to hex string.
     *
     * @param bytes Byte array
     * @return Hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

