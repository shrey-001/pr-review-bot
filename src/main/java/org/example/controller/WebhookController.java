package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook received: " + payload);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Webhook received successfully"
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getWebhookInfo() {
        return ResponseEntity.ok(Map.of(
            "endpoint", "/webhook",
            "method", "POST",
            "description", "Sample webhook endpoint"
        ));
    }
}

