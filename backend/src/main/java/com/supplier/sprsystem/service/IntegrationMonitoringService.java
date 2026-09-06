package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.IntegrationHealthSummaryDto;

public interface IntegrationMonitoringService {

    IntegrationHealthSummaryDto getIntegrationHealthSummary();
}
