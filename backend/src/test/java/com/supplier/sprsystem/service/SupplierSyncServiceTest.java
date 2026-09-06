package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.SupplierSyncItem;
import com.supplier.sprsystem.dto.integration.SupplierSyncRequest;
import com.supplier.sprsystem.dto.integration.SupplierSyncResponse;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.IntegrationSyncHistoryRepository;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.impl.SupplierSyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierSyncServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierCategoryRepository categoryRepository;

    @Mock
    private IntegrationSyncHistoryRepository syncHistoryRepository;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private SupplierSyncServiceImpl supplierSyncService;

    private SupplierCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = SupplierCategory.builder()
                .id(1L)
                .name("IT Hardware")
                .code("CAT_IT")
                .build();
    }

    @Test
    @DisplayName("Sync suppliers with UPSERT creates new and updates existing")
    void testSyncSuppliersUpsert() {
        when(syncHistoryRepository.save(any(IntegrationSyncHistory.class))).thenAnswer(i -> {
            IntegrationSyncHistory h = i.getArgument(0);
            h.setId(1L);
            return h;
        });
        when(categoryRepository.findByNameIgnoreCase("IT Hardware")).thenReturn(Optional.of(sampleCategory));

        Supplier existing = Supplier.builder()
                .id(10L)
                .supplierCode("SUP-001")
                .name("Old Name")
                .email("sup1@example.com")
                .build();

        when(supplierRepository.findBySupplierCode("SUP-001")).thenReturn(Optional.of(existing));
        when(supplierRepository.findBySupplierCode("SUP-002")).thenReturn(Optional.empty());
        when(supplierRepository.findByEmail("sup1@example.com")).thenReturn(Optional.of(existing));
        when(supplierRepository.findByEmail("sup2@example.com")).thenReturn(Optional.empty());

        SupplierSyncItem item1 = new SupplierSyncItem(
                "SUP-001", "Updated Name", "Alice", "sup1@example.com", "12345", "Address 1", "web.com", "IT Hardware", "ACTIVE"
        );
        SupplierSyncItem item2 = new SupplierSyncItem(
                "SUP-002", "Brand New Supplier", "Bob", "sup2@example.com", "67890", "Address 2", "web2.com", "IT Hardware", "ACTIVE"
        );

        SupplierSyncRequest request = new SupplierSyncRequest(
                "SAP ERP",
                IntegrationType.SUPPLIER_CATALOG,
                SyncMode.UPSERT,
                List.of(item1, item2)
        );

        SupplierSyncResponse response = supplierSyncService.syncSuppliers(request, "admin");

        assertNotNull(response);
        assertEquals(SyncStatus.SUCCESS, response.getStatus());
        assertEquals(2, response.getTotalRecords());
        assertEquals(1, response.getCreatedCount());
        assertEquals(1, response.getUpdatedCount());
        assertEquals(0, response.getFailedCount());
        verify(supplierRepository, times(2)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Sync suppliers with CREATE_ONLY skips existing")
    void testSyncSuppliersCreateOnlySkipsExisting() {
        when(syncHistoryRepository.save(any(IntegrationSyncHistory.class))).thenAnswer(i -> {
            IntegrationSyncHistory h = i.getArgument(0);
            h.setId(2L);
            return h;
        });
        when(categoryRepository.findByNameIgnoreCase("IT Hardware")).thenReturn(Optional.of(sampleCategory));

        Supplier existing = Supplier.builder()
                .id(10L)
                .supplierCode("SUP-001")
                .name("Existing")
                .email("sup1@example.com")
                .build();

        when(supplierRepository.findBySupplierCode("SUP-001")).thenReturn(Optional.of(existing));

        SupplierSyncItem item1 = new SupplierSyncItem(
                "SUP-001", "New Name", "Alice", "sup1@example.com", "12345", "Address 1", "web.com", "IT Hardware", "ACTIVE"
        );

        SupplierSyncRequest request = new SupplierSyncRequest(
                "SAP ERP",
                IntegrationType.SUPPLIER_CATALOG,
                SyncMode.CREATE_ONLY,
                List.of(item1)
        );

        SupplierSyncResponse response = supplierSyncService.syncSuppliers(request, "admin");

        assertNotNull(response);
        assertEquals(SyncStatus.SUCCESS, response.getStatus());
        assertEquals(1, response.getTotalRecords());
        assertEquals(0, response.getCreatedCount());
        assertEquals(1, response.getSkippedCount());
        assertEquals(0, response.getFailedCount());
    }
}
