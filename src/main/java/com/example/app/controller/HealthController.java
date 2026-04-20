package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查接口
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", "springboot-app"
        ));
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
                "application", "springboot-app",
                "version", "1.0.0",
                "description", "Flight Recommendation System AI Version",
                "endpoints", Map.of(
                        "health", "/api/health",
                        "swagger-ui", "/swagger-ui.html",
                        "h2-console", "/h2-console",
                        "api-docs", "/v3/api-docs",
                        "authentication", "/api/auth/login"
                ),
                "note", "Use POST /api/auth/login with JSON body for authentication"
        ));
    }
}
