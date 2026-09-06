package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.KpiDefinitionRequest;
import com.supplier.sprsystem.dto.response.KpiCalculationResultResponse;
import com.supplier.sprsystem.dto.response.KpiDefinitionResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.impl.KpiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class KpiServiceTest {

    @Mock
    private KpiDefinitionRepository kpiDefinitionRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierImprovementActionRepository improvementActionRepository;

    @Mock
    private ApprovalTaskRepository approvalTaskRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @InjectMocks
    private KpiServiceImpl kpiService;

    private KpiDefinition avgScoreKpi;
    private KpiDefinition highRiskKpi;

    @BeforeEach
    void setUp() {
        avgScoreKpi = KpiDefinition.builder()
                .id(1L)
                .kpiCode("AVG_SUPPLIER_SCORE")
                .name("Average Supplier Score")
                .description("Mean overall score")
                .category(KpiCategory.PERFORMANCE)
                .calculationType(KpiCalculationType.AVERAGE_SCORE)
                .unit("POINTS")
                .targetValue(85.0)
                .warningThreshold(75.0)
                .criticalThreshold(60.0)
                .higherIsBetter(true)
                .active(true)
                .build();

        highRiskKpi = KpiDefinition.builder()
                .id(2L)
                .kpiCode("HIGH_RISK_SUPPLIER_RATIO")
                .name("High Risk Ratio")
                .description("Proportion of high risk suppliers")
                .category(KpiCategory.RISK)
                .calculationType(KpiCalculationType.RATIO)
                .unit("%")
                .targetValue(5.0)
                .warningThreshold(15.0)
                .criticalThreshold(25.0)
                .higherIsBetter(false)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully retrieve all KPI definitions")
    void shouldGetAllKpiDefinitions() {
        when(kpiDefinitionRepository.findAll()).thenReturn(List.of(avgScoreKpi, highRiskKpi));

        List<KpiDefinitionResponse> result = kpiService.getAllKpiDefinitions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AVG_SUPPLIER_SCORE", result.get(0).getKpiCode());
    }

    @Test
    @DisplayName("Should create KPI definition and validate thresholds")
    void shouldCreateKpiDefinitionSuccessfully() {
        KpiDefinitionRequest request = KpiDefinitionRequest.builder()
                .kpiCode("NEW_KPI")
                .name("New KPI")
                .category(KpiCategory.QUALITY)
                .calculationType(KpiCalculationType.PERCENTAGE)
                .targetValue(90.0)
                .warningThreshold(80.0)
                .criticalThreshold(65.0)
                .higherIsBetter(true)
                .build();

        when(kpiDefinitionRepository.existsByKpiCode("NEW_KPI")).thenReturn(false);
        when(kpiDefinitionRepository.save(any(KpiDefinition.class))).thenAnswer(i -> {
            KpiDefinition k = i.getArgument(0);
            k.setId(10L);
            return k;
        });

        KpiDefinitionResponse response = kpiService.createKpiDefinition(request);

        assertNotNull(response);
        assertEquals("NEW_KPI", response.getKpiCode());
        verify(kpiDefinitionRepository, times(1)).save(any(KpiDefinition.class));
    }

    @Test
    @DisplayName("Should reject KPI creation with invalid critical threshold higher than warning")
    void shouldRejectInvalidThresholds() {
        KpiDefinitionRequest request = KpiDefinitionRequest.builder()
                .kpiCode("INVALID_THRESHOLDS")
                .name("Invalid")
                .category(KpiCategory.QUALITY)
                .calculationType(KpiCalculationType.PERCENTAGE)
                .warningThreshold(70.0)
                .criticalThreshold(85.0) // Invalid for higherIsBetter=true
                .higherIsBetter(true)
                .build();

        assertThrows(BadRequestException.class, () -> kpiService.createKpiDefinition(request));
    }

    @Test
    @DisplayName("Should calculate KPI status GOOD when score exceeds target")
    void shouldCalculateKpiStatusGood() {
        when(kpiDefinitionRepository.findByKpiCode("AVG_SUPPLIER_SCORE")).thenReturn(Optional.of(avgScoreKpi));

        Supplier s1 = Supplier.builder().id(1L).status(SupplierStatus.ACTIVE).overallRating(92.0).totalEvaluations(3).build();
        Supplier s2 = Supplier.builder().id(2L).status(SupplierStatus.ACTIVE).overallRating(88.0).totalEvaluations(2).build();

        when(supplierRepository.findAll()).thenReturn(List.of(s1, s2));

        KpiCalculationResultResponse result = kpiService.calculateKpi("AVG_SUPPLIER_SCORE");

        assertNotNull(result);
        assertEquals(90.0, result.getValue());
        assertEquals(KpiStatus.GOOD, result.getStatus());
        assertEquals(2L, result.getSampleCount());
    }

    @Test
    @DisplayName("Should calculate KPI status WARNING when score is below warning threshold")
    void shouldCalculateKpiStatusWarning() {
        when(kpiDefinitionRepository.findByKpiCode("AVG_SUPPLIER_SCORE")).thenReturn(Optional.of(avgScoreKpi));

        Supplier s1 = Supplier.builder().id(1L).status(SupplierStatus.ACTIVE).overallRating(72.0).totalEvaluations(1).build();
        when(supplierRepository.findAll()).thenReturn(List.of(s1));

        KpiCalculationResultResponse result = kpiService.calculateKpi("AVG_SUPPLIER_SCORE");

        assertNotNull(result);
        assertEquals(72.0, result.getValue());
        assertEquals(KpiStatus.WARNING, result.getStatus());
    }

    @Test
    @DisplayName("Should calculate KPI status CRITICAL when lower is better and value exceeds critical threshold")
    void shouldCalculateKpiStatusCriticalForLowerIsBetter() {
        when(kpiDefinitionRepository.findByKpiCode("HIGH_RISK_SUPPLIER_RATIO")).thenReturn(Optional.of(highRiskKpi));

        Supplier s1 = Supplier.builder().id(1L).status(SupplierStatus.ACTIVE).ratingCategory(RatingCategory.POOR).overallRating(45.0).build();
        Supplier s2 = Supplier.builder().id(2L).status(SupplierStatus.ACTIVE).ratingCategory(RatingCategory.EXCELLENT).overallRating(95.0).build();
        // 1 out of 2 = 50% high risk (critical threshold is 25%)

        when(supplierRepository.findAll()).thenReturn(List.of(s1, s2));

        KpiCalculationResultResponse result = kpiService.calculateKpi("HIGH_RISK_SUPPLIER_RATIO");

        assertNotNull(result);
        assertEquals(50.0, result.getValue());
        assertEquals(KpiStatus.CRITICAL, result.getStatus());
    }

    @Test
    @DisplayName("Should return NO_DATA when no records exist")
    void shouldReturnNoDataWhenEmpty() {
        when(kpiDefinitionRepository.findByKpiCode("AVG_SUPPLIER_SCORE")).thenReturn(Optional.of(avgScoreKpi));
        when(supplierRepository.findAll()).thenReturn(Collections.emptyList());

        KpiCalculationResultResponse result = kpiService.calculateKpi("AVG_SUPPLIER_SCORE");

        assertNotNull(result);
        assertNull(result.getValue());
        assertEquals(KpiStatus.NO_DATA, result.getStatus());
    }

    @Test
    @DisplayName("Should toggle active status")
    void shouldToggleKpiActiveStatus() {
        when(kpiDefinitionRepository.findById(1L)).thenReturn(Optional.of(avgScoreKpi));
        when(kpiDefinitionRepository.save(any(KpiDefinition.class))).thenAnswer(i -> i.getArgument(0));

        KpiDefinitionResponse response = kpiService.toggleKpiDefinition(1L);

        assertNotNull(response);
        assertFalse(response.getActive());
    }
}
