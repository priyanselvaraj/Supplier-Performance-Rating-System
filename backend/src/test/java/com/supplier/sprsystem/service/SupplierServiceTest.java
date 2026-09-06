package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.SupplierRequest;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.SupplierResponse;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.Supplier;
import com.supplier.sprsystem.model.entity.SupplierCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierCategoryRepository categoryRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier sampleSupplier;
    private SupplierCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = SupplierCategory.builder()
                .id(1L)
                .name("Electronics & Hardware")
                .code("CAT-ELEC")
                .active(true)
                .build();

        sampleSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-00001")
                .name("Apex Chipsets Ltd")
                .contactPerson("Sarah Jenkins")
                .email("contact@apexchips.com")
                .phone("+1 555-0199")
                .city("San Jose")
                .country("United States")
                .category(sampleCategory)
                .status(SupplierStatus.ACTIVE)
                .overallRating(88.5)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(2)
                .build();
    }

    @Test
    @DisplayName("Test GetAllSuppliers returns list")
    void testGetAllSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(sampleSupplier));

        List<SupplierResponse> responses = supplierService.getAllSuppliers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("SUP-00001", responses.get(0).getSupplierCode());
        assertEquals("Apex Chipsets Ltd", responses.get(0).getName());
        assertEquals("Apex Chipsets Ltd", responses.get(0).getSupplierName());
    }

    @Test
    @DisplayName("Test GetSupplierById Success")
    void testGetSupplierById_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));

        SupplierResponse response = supplierService.getSupplierById(1L);

        assertNotNull(response);
        assertEquals("SUP-00001", response.getSupplierCode());
        assertEquals("contact@apexchips.com", response.getEmail());
    }

    @Test
    @DisplayName("Test GetSupplierById with non-existent ID throws ResourceNotFoundException")
    void testGetSupplierById_NotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(99L));
    }

    @Test
    @DisplayName("Test Create Supplier Success")
    void testCreateSupplier_Success() {
        SupplierRequest request = SupplierRequest.builder()
                .name("Global Logistics Inc")
                .contactPerson("David Miller")
                .email("info@globallogistics.com")
                .phone("+1 312-555-0188")
                .city("Chicago")
                .country("United States")
                .categoryId(1L)
                .active(true)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.existsByEmail("info@globallogistics.com")).thenReturn(false);
        when(supplierRepository.count()).thenReturn(1L);
        when(supplierRepository.existsBySupplierCode(anyString())).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier s = invocation.getArgument(0);
            s.setId(2L);
            return s;
        });

        SupplierResponse response = supplierService.createSupplier(request);

        assertNotNull(response);
        assertNotNull(response.getSupplierCode());
        assertEquals("Global Logistics Inc", response.getName());
        assertTrue(response.isActive());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Test Create Supplier with invalid category throws ResourceNotFoundException")
    void testCreateSupplier_InvalidCategory_ThrowsException() {
        SupplierRequest request = SupplierRequest.builder()
                .name("Invalid Category Supplier")
                .email("invalid@test.com")
                .categoryId(99L)
                .build();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supplierService.createSupplier(request));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Create Supplier with duplicate email throws DuplicateResourceException")
    void testCreateSupplier_DuplicateEmail_ThrowsException() {
        SupplierRequest request = SupplierRequest.builder()
                .name("Duplicate Email Supplier")
                .email("contact@apexchips.com")
                .categoryId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.existsByEmail("contact@apexchips.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> supplierService.createSupplier(request));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Update Supplier Success")
    void testUpdateSupplier_Success() {
        SupplierRequest request = SupplierRequest.builder()
                .name("Apex Chipsets Updated")
                .contactPerson("Sarah J. Connor")
                .email("contact@apexchips.com")
                .categoryId(1L)
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(sampleSupplier);

        SupplierResponse response = supplierService.updateSupplier(1L, request);

        assertNotNull(response);
        assertEquals("Apex Chipsets Updated", response.getName());
        verify(supplierRepository, times(1)).save(sampleSupplier);
    }

    @Test
    @DisplayName("Test Activate Supplier")
    void testActivateSupplier() {
        sampleSupplier.setStatus(SupplierStatus.INACTIVE);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(sampleSupplier);

        SupplierResponse response = supplierService.activateSupplier(1L);

        assertNotNull(response);
        assertEquals(SupplierStatus.ACTIVE, response.getStatus());
        assertTrue(response.isActive());
    }

    @Test
    @DisplayName("Test Deactivate Supplier")
    void testDeactivateSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(sampleSupplier);

        SupplierResponse response = supplierService.deactivateSupplier(1L);

        assertNotNull(response);
        assertEquals(SupplierStatus.INACTIVE, response.getStatus());
        assertFalse(response.isActive());
    }

    @Test
    @DisplayName("Test Delete Supplier Success")
    void testDeleteSupplier_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        doNothing().when(supplierRepository).delete(sampleSupplier);

        assertDoesNotThrow(() -> supplierService.deleteSupplier(1L));
        verify(supplierRepository, times(1)).delete(sampleSupplier);
    }

    @Test
    @DisplayName("Test Search Suppliers")
    void testSearchSuppliers() {
        Page<Supplier> page = new PageImpl<>(List.of(sampleSupplier));
        when(supplierRepository.searchSuppliers(eq("apex"), any(Pageable.class))).thenReturn(page);

        PaginatedResponse<SupplierResponse> result = supplierService.searchSuppliers("apex", 0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Apex Chipsets Ltd", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Test Filter Suppliers")
    void testFilterSuppliers() {
        Page<Supplier> page = new PageImpl<>(List.of(sampleSupplier));
        when(supplierRepository.filterSuppliers(eq(1L), eq(SupplierStatus.ACTIVE), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PaginatedResponse<SupplierResponse> result = supplierService.filterSuppliers(1L, true, null, null, 0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
