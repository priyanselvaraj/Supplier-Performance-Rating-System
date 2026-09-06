package com.supplier.sprsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.integration.SupplierSyncItem;
import com.supplier.sprsystem.dto.integration.SupplierSyncRequest;
import com.supplier.sprsystem.dto.integration.SupplierSyncResponse;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.IntegrationSyncHistoryRepository;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.SupplierSyncService;
import com.supplier.sprsystem.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierSyncServiceImpl implements SupplierSyncService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierSyncServiceImpl.class);

    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository categoryRepository;
    private final IntegrationSyncHistoryRepository syncHistoryRepository;
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SupplierSyncServiceImpl(SupplierRepository supplierRepository,
                                   SupplierCategoryRepository categoryRepository,
                                   IntegrationSyncHistoryRepository syncHistoryRepository,
                                   WebhookService webhookService) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.syncHistoryRepository = syncHistoryRepository;
        this.webhookService = webhookService;
    }

    @Override
    public SupplierSyncResponse syncSuppliers(SupplierSyncRequest request, String executedByName) {
        long startTime = System.currentTimeMillis();

        IntegrationSyncHistory history = new IntegrationSyncHistory(
                request.getSourceSystem(),
                request.getIntegrationType() != null ? request.getIntegrationType() : IntegrationType.SUPPLIER_CATALOG,
                request.getSyncMode(),
                executedByName != null ? executedByName : "System Sync Engine"
        );
        history.setTotalRecords(request.getSuppliers() != null ? request.getSuppliers().size() : 0);
        history = syncHistoryRepository.save(history);

        int created = 0;
        int updated = 0;
        int skipped = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        if (request.getSuppliers() == null || request.getSuppliers().isEmpty()) {
            history.markCompleted(SyncStatus.SUCCESS, "Empty batch received", null);
            syncHistoryRepository.save(history);
            return new SupplierSyncResponse(
                    history.getId(), request.getSourceSystem(), request.getSyncMode(),
                    SyncStatus.SUCCESS, 0, 0, 0, 0, 0,
                    System.currentTimeMillis() - startTime, errors, "No suppliers to synchronize"
            );
        }

        for (SupplierSyncItem item : request.getSuppliers()) {
            try {
                if (!StringUtils.hasText(item.getSupplierCode()) || !StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getEmail())) {
                    failed++;
                    errors.add("Record missing required fields (code, name, or email): " + item.getSupplierCode());
                    continue;
                }

                SupplierCategory category = resolveCategory(item.getCategoryName());
                Optional<Supplier> existingByCodeOpt = supplierRepository.findBySupplierCode(item.getSupplierCode());
                Optional<Supplier> existingByEmailOpt = supplierRepository.findByEmail(item.getEmail());

                switch (request.getSyncMode()) {
                    case CREATE_ONLY -> {
                        if (existingByCodeOpt.isPresent()) {
                            skipped++;
                        } else if (existingByEmailOpt.isPresent()) {
                            failed++;
                            errors.add("Supplier code " + item.getSupplierCode() + " failed: Email '" + item.getEmail() + "' already in use by another supplier");
                        } else {
                            Supplier newSupplier = createSupplierFromItem(item, category);
                            supplierRepository.save(newSupplier);
                            created++;
                            try {
                                webhookService.dispatchEvent(WebhookEventType.SUPPLIER_CREATED, newSupplier.getSupplierCode());
                            } catch (Exception ignored) {}
                        }
                    }
                    case UPDATE_EXISTING -> {
                        if (existingByCodeOpt.isEmpty()) {
                            skipped++;
                        } else {
                            Supplier existing = existingByCodeOpt.get();
                            if (existingByEmailOpt.isPresent() && !existingByEmailOpt.get().getId().equals(existing.getId())) {
                                failed++;
                                errors.add("Supplier " + item.getSupplierCode() + " update failed: Email '" + item.getEmail() + "' belongs to another supplier");
                            } else {
                                updateSupplierFromItem(existing, item, category);
                                supplierRepository.save(existing);
                                updated++;
                                try {
                                    webhookService.dispatchEvent(WebhookEventType.SUPPLIER_UPDATED, existing.getSupplierCode());
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                    case UPSERT -> {
                        if (existingByCodeOpt.isPresent()) {
                            Supplier existing = existingByCodeOpt.get();
                            if (existingByEmailOpt.isPresent() && !existingByEmailOpt.get().getId().equals(existing.getId())) {
                                failed++;
                                errors.add("Supplier " + item.getSupplierCode() + " upsert failed: Email '" + item.getEmail() + "' belongs to another supplier");
                            } else {
                                updateSupplierFromItem(existing, item, category);
                                supplierRepository.save(existing);
                                updated++;
                                try {
                                    webhookService.dispatchEvent(WebhookEventType.SUPPLIER_UPDATED, existing.getSupplierCode());
                                } catch (Exception ignored) {}
                            }
                        } else {
                            if (existingByEmailOpt.isPresent()) {
                                failed++;
                                errors.add("Supplier code " + item.getSupplierCode() + " creation failed: Email '" + item.getEmail() + "' belongs to another supplier");
                            } else {
                                Supplier newSupplier = createSupplierFromItem(item, category);
                                supplierRepository.save(newSupplier);
                                created++;
                                try {
                                    webhookService.dispatchEvent(WebhookEventType.SUPPLIER_CREATED, newSupplier.getSupplierCode());
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            } catch (Exception e) {
                failed++;
                errors.add("Error processing supplier " + item.getSupplierCode() + ": " + e.getMessage());
                logger.error("Error processing sync item {}: {}", item.getSupplierCode(), e.getMessage());
            }
        }

        history.setCreatedCount(created);
        history.setUpdatedCount(updated);
        history.setSkippedCount(skipped);
        history.setFailedCount(failed);

        SyncStatus finalStatus;
        if (failed == 0) {
            finalStatus = SyncStatus.SUCCESS;
        } else if (created > 0 || updated > 0 || skipped > 0) {
            finalStatus = SyncStatus.PARTIAL_SUCCESS;
        } else {
            finalStatus = SyncStatus.FAILED;
        }

        String detailsJson = null;
        try {
            if (!errors.isEmpty()) {
                detailsJson = objectMapper.writeValueAsString(errors);
            }
        } catch (Exception ignored) {}

        String errorSummary = errors.isEmpty() ? null : errors.size() + " record(s) failed during sync";
        history.markCompleted(finalStatus, errorSummary, detailsJson);
        syncHistoryRepository.save(history);

        long duration = System.currentTimeMillis() - startTime;
        String message = String.format("Synchronization finished. %d created, %d updated, %d skipped, %d failed in %d ms",
                created, updated, skipped, failed, duration);

        return new SupplierSyncResponse(
                history.getId(),
                request.getSourceSystem(),
                request.getSyncMode(),
                finalStatus,
                request.getSuppliers().size(),
                created,
                updated,
                skipped,
                failed,
                duration,
                errors,
                message
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntegrationSyncHistory> getRecentSyncHistories() {
        return syncHistoryRepository.findTop20ByOrderByStartedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public IntegrationSyncHistory getSyncHistoryById(Long id) {
        return syncHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sync history not found with ID: " + id));
    }

    private SupplierCategory resolveCategory(String categoryName) {
        if (StringUtils.hasText(categoryName)) {
            Optional<SupplierCategory> catOpt = categoryRepository.findByNameIgnoreCase(categoryName.trim());
            if (catOpt.isPresent()) {
                return catOpt.get();
            }
            // If category doesn't exist, create it automatically
            String catCode = categoryName.trim().toUpperCase().replaceAll("[^A-Z0-9]", "_");
            if (catCode.length() > 20) catCode = catCode.substring(0, 20);
            SupplierCategory newCat = SupplierCategory.builder()
                    .code(catCode + "_" + System.currentTimeMillis() % 1000)
                    .name(categoryName.trim())
                    .description("Auto-created from Integration Sync")
                    .active(true)
                    .build();
            return categoryRepository.save(newCat);
        }

        // Fallback to existing category or create General
        List<SupplierCategory> allCats = categoryRepository.findAll();
        if (!allCats.isEmpty()) {
            return allCats.get(0);
        }

        SupplierCategory general = SupplierCategory.builder()
                .code("CAT_GEN")
                .name("General Supplies")
                .description("Default general category")
                .active(true)
                .build();
        return categoryRepository.save(general);
    }

    private Supplier createSupplierFromItem(SupplierSyncItem item, SupplierCategory category) {
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(item.getSupplierCode().trim());
        supplier.setName(item.getName().trim());
        supplier.setContactPerson(item.getContactPerson());
        supplier.setEmail(item.getEmail().trim().toLowerCase());
        supplier.setPhone(item.getPhone());
        supplier.setAddress(item.getAddress());
        supplier.setWebsite(item.getWebsite());
        supplier.setCategory(category);

        SupplierStatus status = SupplierStatus.ACTIVE;
        if (StringUtils.hasText(item.getStatus())) {
            try {
                status = SupplierStatus.valueOf(item.getStatus().trim().toUpperCase());
            } catch (Exception ignored) {}
        }
        supplier.setStatus(status);

        return supplier;
    }

    private void updateSupplierFromItem(Supplier supplier, SupplierSyncItem item, SupplierCategory category) {
        supplier.setName(item.getName().trim());
        if (item.getContactPerson() != null) supplier.setContactPerson(item.getContactPerson());
        supplier.setEmail(item.getEmail().trim().toLowerCase());
        if (item.getPhone() != null) supplier.setPhone(item.getPhone());
        if (item.getAddress() != null) supplier.setAddress(item.getAddress());
        if (item.getWebsite() != null) supplier.setWebsite(item.getWebsite());
        if (category != null) supplier.setCategory(category);

        if (StringUtils.hasText(item.getStatus())) {
            try {
                supplier.setStatus(SupplierStatus.valueOf(item.getStatus().trim().toUpperCase()));
            } catch (Exception ignored) {}
        }
    }
}
