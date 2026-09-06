package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.MonitoringSummaryResponse;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Real-Time Monitoring", description = "Live health pulse, critical risk tracking, and operational throughput monitoring")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final UserRepository userRepository;

    public MonitoringController(MonitoringService monitoringService, UserRepository userRepository) {
        this.monitoringService = monitoringService;
        this.userRepository = userRepository;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get comprehensive real-time system monitoring metrics and health status")
    public ResponseEntity<ApiResponse<MonitoringSummaryResponse>> getMonitoringSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long currentUserId = getUserId(userDetails);
        MonitoringSummaryResponse response = monitoringService.getMonitoringSummary(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Monitoring metrics retrieved successfully", response));
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) return 1L;
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        return user != null ? user.getId() : 1L;
    }
}
