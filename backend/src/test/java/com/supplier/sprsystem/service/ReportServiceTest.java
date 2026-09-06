package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierPerformanceRatingRepository performanceRatingRepository;

    @Mock
    private SupplierRatingService supplierRatingService;

    @Mock
    private DashboardAnalyticsService dashboardAnalyticsService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Supplier topSupplier;
    private Supplier lowSupplier;
    private SupplierEvaluation eval1;
    private SupplierPerformanceRating rating1;

    @BeforeEach
    void setUp() {
        SupplierCategory category = SupplierCategory.builder().id(1L).name("Electronics").build();

        topSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-01")
                .name("Top Tech")
                .overallRating(92.0)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(3)
                .category(category)
                .status(SupplierStatus.ACTIVE)
                .build();

        lowSupplier = Supplier.builder()
                .id(2L)
                .supplierCode("SUP-02")
                .name("Low Tech")
                .overallRating(42.0)
                .ratingCategory(RatingCategory.POOR)
                .totalEvaluations(1)
                .category(category)
                .status(SupplierStatus.ACTIVE)
                .build();

        EvaluationCriteria criteria = EvaluationCriteria.builder()
                .id(1L)
                .name("Quality")
                .code("CRIT-01")
                .build();

        EvaluationScore score = EvaluationScore.builder()
                .id(1L)
                .criteria(criteria)
                .weight(100.0)
                .maxScore(100.0)
                .scoreObtained(92.0)
                .weightedScore(92.0)
                .remarks("Great quality")
                .build();

        User evaluator = User.builder()
                .id(1L)
                .fullName("John Evaluator")
                .username("john")
                .build();

        eval1 = SupplierEvaluation.builder()
                .id(10L)
                .evaluationCode("EV-2026-001")
                .supplier(topSupplier)
                .evaluator(evaluator)
                .evaluationDate(LocalDate.of(2026, 8, 20))
                .evaluationPeriod("Q3 2026")
                .status(EvaluationStatus.COMPLETED)
                .totalWeightedScore(92.0)
                .scores(List.of(score))
                .generalComments("Overall solid performance")
                .strengths("Prompt delivery")
                .areasForImprovement("None")
                .recommendation("Renew contract")
                .build();

        rating1 = SupplierPerformanceRating.builder()
                .id(100L)
                .supplier(topSupplier)
                .evaluation(eval1)
                .score(92.0)
                .rating(SupplierRating.EXCELLENT)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .ratingDate(LocalDate.of(2026, 8, 20))
                .build();
    }

    @Test
    @DisplayName("1. Generate Supplier Performance Report")
    void testGetSupplierPerformanceReport() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(topSupplier));
        when(supplierRatingService.getSupplierPerformanceSummary(1L)).thenReturn(
                SupplierPerformanceSummaryResponse.builder()
                        .supplierId(1L)
                        .supplierCode("SUP-01")
                        .supplierName("Top Tech")
                        .latestScore(92.0)
                        .latestRating(SupplierRating.EXCELLENT)
                        .latestPerformanceStatus(PerformanceStatus.HIGH_PERFORMING)
                        .performanceTrend(PerformanceTrend.STABLE)
                        .ratingDate(LocalDate.of(2026, 8, 20))
                        .build()
        );
        when(performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(rating1));

        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(1L, null, null);

        assertNotNull(report);
        assertEquals("Top Tech", report.getSupplierName());
        assertEquals(92.0, report.getLatestScore());
        assertEquals("Excellent", report.getLatestRatingDisplayName());
        assertEquals(1, report.getRatingHistory().size());
    }

    @Test
    @DisplayName("2. Supplier Not Found throws ResourceNotFoundException")
    void testGetSupplierPerformanceReport_NotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.getSupplierPerformanceReport(99L, null, null));
    }

    @Test
    @DisplayName("3. Generate Supplier Evaluation Report")
    void testGetSupplierEvaluationReport() {
        when(evaluationRepository.findById(10L)).thenReturn(Optional.of(eval1));

        SupplierEvaluationReportResponse report = reportService.getSupplierEvaluationReport(10L);

        assertNotNull(report);
        assertEquals("EV-2026-001", report.getEvaluationCode());
        assertEquals("Top Tech", report.getSupplierName());
        assertEquals("John Evaluator", report.getEvaluatorName());
        assertEquals(1, report.getCriteriaScores().size());
        assertEquals("Quality", report.getCriteriaScores().get(0).getCriteriaName());
    }

    @Test
    @DisplayName("4. Evaluation Not Found throws ResourceNotFoundException")
    void testGetSupplierEvaluationReport_NotFound() {
        when(evaluationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.getSupplierEvaluationReport(999L));
    }

    @Test
    @DisplayName("5. Generate Supplier Rating History Report")
    void testGetSupplierRatingHistoryReport() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(rating1));

        List<SupplierPerformanceRatingResponse> history = reportService.getSupplierRatingHistoryReport(1L, null, null);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(92.0, history.get(0).getScore());
    }

    @Test
    @DisplayName("6. Generate Overall Performance Report")
    void testGetOverallPerformanceReport() {
        when(supplierRepository.count()).thenReturn(2L);
        when(supplierRepository.countByStatus(SupplierStatus.ACTIVE)).thenReturn(2L);
        when(supplierRepository.countByStatus(SupplierStatus.INACTIVE)).thenReturn(0L);

        when(dashboardAnalyticsService.getPerformanceAnalytics(null, null)).thenReturn(
                SupplierPerformanceAnalyticsResponse.builder()
                        .averageScore(67.0)
                        .highestScore(92.0)
                        .lowestScore(42.0)
                        .totalRatedSuppliers(2)
                        .build()
        );
        when(dashboardAnalyticsService.getRatingDistribution()).thenReturn(Collections.emptyList());
        when(dashboardAnalyticsService.getPerformanceStatusDistribution()).thenReturn(Collections.emptyList());
        when(dashboardAnalyticsService.getTopPerformingSuppliers(5)).thenReturn(Collections.emptyList());
        when(dashboardAnalyticsService.getLowPerformingSuppliers(5)).thenReturn(Collections.emptyList());

        OverallPerformanceReportResponse report = reportService.getOverallPerformanceReport(null, null);

        assertNotNull(report);
        assertEquals(2L, report.getTotalSuppliers());
        assertEquals(67.0, report.getAveragePerformanceScore());
    }

    @Test
    @DisplayName("7. Generate Evaluation Summary Report")
    void testGetEvaluationSummaryReport() {
        when(evaluationRepository.findAll()).thenReturn(List.of(eval1));

        EvaluationSummaryReportResponse summary = reportService.getEvaluationSummaryReport(null, null);

        assertNotNull(summary);
        assertEquals(1L, summary.getTotalEvaluations());
        assertEquals(1L, summary.getCompletedEvaluations());
        assertEquals(92.0, summary.getAverageEvaluationScore());
    }

    @Test
    @DisplayName("8. Date Filtering with Valid Range")
    void testDateFiltering() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(topSupplier));
        when(supplierRatingService.getSupplierPerformanceSummary(1L)).thenReturn(
                SupplierPerformanceSummaryResponse.builder().supplierId(1L).build()
        );
        when(performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(rating1));

        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(
                1L, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertNotNull(report);
        assertEquals(1, report.getRatingHistory().size());
    }

    @Test
    @DisplayName("9. Invalid Date Range throws BadRequestException")
    void testInvalidDateRange() {
        assertThrows(BadRequestException.class, () ->
                reportService.getSupplierPerformanceReport(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 8, 1)));
    }

    @Test
    @DisplayName("10. Backward Compatibility - GetTopPerformingSuppliers")
    void testGetTopPerformingSuppliers() {
        when(supplierRepository.findTop5ByOrderByOverallRatingDesc()).thenReturn(List.of(topSupplier));

        List<SupplierResponse> result = reportService.getTopPerformingSuppliers(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Top Tech", result.get(0).getName());
    }

    @Test
    @DisplayName("11. Backward Compatibility - GeneratePerformanceReport")
    void testGeneratePerformanceReport() {
        when(supplierRepository.filterSuppliersList(any(), any(), any(), any()))
                .thenReturn(List.of(topSupplier, lowSupplier));
        when(evaluationRepository.filterEvaluationsList(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        PerformanceReportResponse report = reportService.generatePerformanceReport(
                null, null, null, null, null, null);

        assertNotNull(report);
        assertEquals(2, report.getTotalSuppliersEvaluated());
        assertEquals(67.0, report.getOverallSystemAverageScore());
    }
}
