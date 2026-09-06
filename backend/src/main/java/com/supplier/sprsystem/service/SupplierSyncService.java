package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.SupplierSyncRequest;
import com.supplier.sprsystem.dto.integration.SupplierSyncResponse;
import com.supplier.sprsystem.model.entity.IntegrationSyncHistory;

import java.util.List;

public interface SupplierSyncService {

    SupplierSyncResponse syncSuppliers(SupplierSyncRequest request, String executedByName);

    List<IntegrationSyncHistory> getRecentSyncHistories();

    IntegrationSyncHistory getSyncHistoryById(Long id);
}
