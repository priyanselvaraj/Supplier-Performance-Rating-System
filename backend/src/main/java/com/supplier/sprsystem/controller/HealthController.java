package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Application health and status monitoring")
public class HealthController {

    @GetMapping(value = {"/api/health", "/api/v1/health"})
    @Operation(summary = "Check application health and operational status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("application", "Supplier Performance Rating System");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("timestamp", LocalDateTime.now().toString());
        healthInfo.put("environment", "production-ready");

        return ResponseEntity.ok(ApiResponse.success("Application is running normally", healthInfo));
    }
}
