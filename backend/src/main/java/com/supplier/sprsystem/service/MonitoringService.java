package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.MonitoringSummaryResponse;

public interface MonitoringService {

    MonitoringSummaryResponse getMonitoringSummary(Long currentUserId);
}
