package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.EvaluationCriteriaRequest;
import com.supplier.sprsystem.dto.response.EvaluationCriteriaResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.EvaluationCriteria;
import com.supplier.sprsystem.model.entity.EvaluationScore;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.EvaluationScoreRepository;
import com.supplier.sprsystem.service.impl.EvaluationCriteriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationCriteriaServiceTest {

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @Mock
    private EvaluationScoreRepository scoreRepository;

    @InjectMocks
    private EvaluationCriteriaServiceImpl criteriaService;

    private EvaluationCriteria sampleCriteria;

    @BeforeEach
    void setUp() {
        sampleCriteria = EvaluationCriteria.builder()
                .id(1L)
                .name("Quality of Goods")
                .code("CRIT-QUAL")
                .description("Defect rates and conformance")
                .weight(30.0)
                .maxScore(100.0)
                .displayOrder(1)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Test GetAllCriteria returns list ordered by display order")
    void testGetAllCriteria() {
        when(criteriaRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(sampleCriteria));

        List<EvaluationCriteriaResponse> result = criteriaService.getAllCriteria();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Quality of Goods", result.get(0).getName());
        assertEquals(30.0, result.get(0).getWeight());
    }

    @Test
    @DisplayName("Test GetActiveCriteria returns only active criteria")
    void testGetActiveCriteria() {
        when(criteriaRepository.findByActiveOrderByDisplayOrderAsc(true)).thenReturn(List.of(sampleCriteria));

        List<EvaluationCriteriaResponse> result = criteriaService.getActiveCriteria();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("Test GetCriteriaById Success")
    void testGetCriteriaById_Success() {
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));

        EvaluationCriteriaResponse response = criteriaService.getCriteriaById(1L);

        assertNotNull(response);
        assertEquals("Quality of Goods", response.getName());
    }

    @Test
    @DisplayName("Test GetCriteriaById NotFound throws ResourceNotFoundException")
    void testGetCriteriaById_NotFound() {
        when(criteriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> criteriaService.getCriteriaById(99L));
    }

    @Test
    @DisplayName("Test Create Criteria Success")
    void testCreateCriteria_Success() {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .name("Delivery Performance")
                .code("CRIT-DELV")
                .description("On-time delivery performance")
                .weight(25.0)
                .maxScore(100.0)
                .displayOrder(2)
                .active(true)
                .build();

        when(criteriaRepository.existsByNameIgnoreCase("Delivery Performance")).thenReturn(false);
        when(criteriaRepository.existsByCode("CRIT-DELV")).thenReturn(false);
        when(criteriaRepository.save(any(EvaluationCriteria.class))).thenAnswer(inv -> {
            EvaluationCriteria c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        EvaluationCriteriaResponse response = criteriaService.createCriteria(request);

        assertNotNull(response);
        assertEquals("Delivery Performance", response.getName());
        assertEquals(25.0, response.getWeight());
        verify(criteriaRepository, times(1)).save(any(EvaluationCriteria.class));
    }

    @Test
    @DisplayName("Test Create Criteria with duplicate name throws DuplicateResourceException")
    void testCreateCriteria_DuplicateName_ThrowsException() {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .name("Quality of Goods")
                .weight(30.0)
                .build();

        when(criteriaRepository.existsByNameIgnoreCase("Quality of Goods")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> criteriaService.createCriteria(request));
        verify(criteriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test Update Criteria Success")
    void testUpdateCriteria_Success() {
        EvaluationCriteriaRequest request = EvaluationCriteriaRequest.builder()
                .name("Product Quality")
                .code("CRIT-QUAL")
                .description("Updated description")
                .weight(35.0)
                .maxScore(100.0)
                .build();

        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(criteriaRepository.existsByNameIgnoreCase("Product Quality")).thenReturn(false);
        when(criteriaRepository.save(any(EvaluationCriteria.class))).thenReturn(sampleCriteria);

        EvaluationCriteriaResponse response = criteriaService.updateCriteria(1L, request);

        assertNotNull(response);
        verify(criteriaRepository, times(1)).save(sampleCriteria);
    }

    @Test
    @DisplayName("Test Activate Criteria")
    void testActivateCriteria() {
        sampleCriteria.setActive(false);
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(criteriaRepository.save(any(EvaluationCriteria.class))).thenReturn(sampleCriteria);

        EvaluationCriteriaResponse response = criteriaService.activateCriteria(1L);

        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    @DisplayName("Test Deactivate Criteria")
    void testDeactivateCriteria() {
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(criteriaRepository.save(any(EvaluationCriteria.class))).thenReturn(sampleCriteria);

        EvaluationCriteriaResponse response = criteriaService.deactivateCriteria(1L);

        assertNotNull(response);
        assertFalse(response.isActive());
    }

    @Test
    @DisplayName("Test Toggle Criteria Active")
    void testToggleCriteriaActive() {
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(criteriaRepository.save(any(EvaluationCriteria.class))).thenReturn(sampleCriteria);

        EvaluationCriteriaResponse response = criteriaService.toggleCriteriaActive(1L);

        assertNotNull(response);
        assertFalse(response.isActive());
    }

    @Test
    @DisplayName("Test Get Total Active Weights")
    void testGetTotalActiveWeights() {
        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);

        Double totalWeight = criteriaService.getTotalActiveWeights();

        assertEquals(100.0, totalWeight);
    }

    @Test
    @DisplayName("Test Delete Criteria Safely when not used by evaluations")
    void testDeleteCriteria_Success() {
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(scoreRepository.findByCriteriaId(1L)).thenReturn(Collections.emptyList());
        doNothing().when(criteriaRepository).delete(sampleCriteria);

        assertDoesNotThrow(() -> criteriaService.deleteCriteria(1L));
        verify(criteriaRepository, times(1)).delete(sampleCriteria);
    }

    @Test
    @DisplayName("Test Delete Criteria throws BadRequestException when used by evaluations")
    void testDeleteCriteria_WithScores_ThrowsException() {
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(sampleCriteria));
        when(scoreRepository.findByCriteriaId(1L)).thenReturn(List.of(new EvaluationScore()));

        assertThrows(BadRequestException.class, () -> criteriaService.deleteCriteria(1L));
        verify(criteriaRepository, never()).delete(any());
    }
}
