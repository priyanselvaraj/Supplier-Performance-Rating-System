package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.SupplierCategoryRequest;
import com.supplier.sprsystem.dto.response.SupplierCategoryResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.SupplierCategory;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.impl.SupplierCategoryServiceImpl;
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
public class SupplierCategoryServiceTest {

    @Mock
    private SupplierCategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierCategoryServiceImpl categoryService;

    private SupplierCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = SupplierCategory.builder()
                .id(1L)
                .name("Electronics & Hardware")
                .code("CAT-ELEC")
                .description("Computer chips and electronic parts")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Test GetAllCategories returns list")
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));

        List<SupplierCategoryResponse> responses = categoryService.getAllCategories();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Electronics & Hardware", responses.get(0).getName());
        assertTrue(responses.get(0).isActive());
    }

    @Test
    @DisplayName("Test GetCategoryById Success")
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));

        SupplierCategoryResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics & Hardware", response.getName());
    }

    @Test
    @DisplayName("Test GetCategoryById with non-existent ID throws ResourceNotFoundException")
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    @DisplayName("Test Create Category Success")
    void testCreateCategory_Success() {
        SupplierCategoryRequest request = SupplierCategoryRequest.builder()
                .name("Raw Materials")
                .code("CAT-RAW")
                .description("Metals and chemicals")
                .active(true)
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Raw Materials")).thenReturn(false);
        when(categoryRepository.existsByCode("CAT-RAW")).thenReturn(false);
        when(categoryRepository.save(any(SupplierCategory.class))).thenAnswer(invocation -> {
            SupplierCategory c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });

        SupplierCategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Raw Materials", response.getName());
        assertEquals("CAT-RAW", response.getCode());
        assertTrue(response.isActive());
        verify(categoryRepository, times(1)).save(any(SupplierCategory.class));
    }

    @Test
    @DisplayName("Test Create Category with duplicate name throws DuplicateResourceException")
    void testCreateCategory_DuplicateName_ThrowsException() {
        SupplierCategoryRequest request = SupplierCategoryRequest.builder()
                .name("Electronics & Hardware")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Electronics & Hardware")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Update Category Success")
    void testUpdateCategory_Success() {
        SupplierCategoryRequest request = SupplierCategoryRequest.builder()
                .name("Electronics & High-Tech")
                .code("CAT-ELEC")
                .description("Updated description")
                .active(true)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.existsByNameIgnoreCase("Electronics & High-Tech")).thenReturn(false);
        when(categoryRepository.save(any(SupplierCategory.class))).thenReturn(sampleCategory);

        SupplierCategoryResponse response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        verify(categoryRepository, times(1)).save(sampleCategory);
    }

    @Test
    @DisplayName("Test Delete Category Success when no suppliers attached")
    void testDeleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.countByCategoryId(1L)).thenReturn(0L);
        when(supplierRepository.existsByCategoryId(1L)).thenReturn(false);
        doNothing().when(categoryRepository).delete(sampleCategory);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).delete(sampleCategory);
    }

    @Test
    @DisplayName("Test Delete Category with associated suppliers throws BadRequestException")
    void testDeleteCategory_WithSuppliers_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(supplierRepository.countByCategoryId(1L)).thenReturn(3L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L));
        assertTrue(ex.getMessage().contains("suppliers are associated"));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test Activate Category")
    void testActivateCategory() {
        sampleCategory.setActive(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(SupplierCategory.class))).thenReturn(sampleCategory);

        SupplierCategoryResponse response = categoryService.activateCategory(1L);

        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    @DisplayName("Test Deactivate Category")
    void testDeactivateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(SupplierCategory.class))).thenReturn(sampleCategory);

        SupplierCategoryResponse response = categoryService.deactivateCategory(1L);

        assertNotNull(response);
        assertFalse(response.isActive());
    }
}
