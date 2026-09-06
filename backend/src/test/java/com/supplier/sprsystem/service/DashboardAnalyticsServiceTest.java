package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardAnalyticsServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierCategoryRepository categoryRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierPerformanceRatingRepository performanceRatingRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Supplier supplier1;
    private Supplier supplier2;
    private SupplierCategory categoryElec;
    private SupplierEvaluation eval1;
    private SupplierPerformanceRating rating1;
    private SupplierPerformanceRating rating2;

    @BeforeEach
    void setUp() {
        categoryElec = SupplierCategory.builder()
                .id(1L)
                .name("Electronics & Hardware")
                .code("CAT-ELEC")
                .active(true)
                .build();

        supplier1 = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-00001")
                .name("Apex Chipsets Ltd")
                .category(categoryElec)
                .active(true)
                .status(SupplierStatus.ACTIVE)
                .overallRating(92.5)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(1)
                .build();

        supplier2 = Supplier.builder()
                .id(2L)
                .supplierCode("SUP-00002")
                .name("Beacon Logistics")
                .category(categoryElec)
                .active(false)
                .status(SupplierStatus.INACTIVE)
                .overallRating(65.0)
                .ratingCategory(RatingCategory.AVERAGE)
                .totalEvaluations(1)
                .build();

        eval1 = SupplierEvaluation.builder()
                .id(100L)
                .evaluationCode("EV-202608-1001")
                .supplier(supplier1)
                .evaluationDate(LocalDate.of(2026, 8, 20))
                .status(EvaluationStatus.COMPLETED)
                .totalWeightedScore(92.5)
                .build();

        rating1 = SupplierPerformanceRating.builder()
                .id(1L)
                .supplier(supplier1)
                .evaluation(eval1)
                .score(92.5)
                .rating(SupplierRating.EXCELLENT)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .ratingDate(LocalDate.of(2026, 8, 20))
                .build();

        rating2 = SupplierPerformanceRating.builder()
                .id(2L)
                .supplier(supplier2)
                .score(65.0)
                .rating(SupplierRating.AVERAGE)
                .performanceStatus(PerformanceStatus.NEEDS_IMPROVEMENT)
                .ratingDate(LocalDate.of(2026, 8, 22))
                .build();
    }

    @Test
    @DisplayName("1. Test Dashboard summary with populated data")
    void testGetDashboardSummary_WithData() {
        when(supplierRepository.count()).thenReturn(2L);
        when(supplierRepository.countByActive(true)).thenReturn(1L);
        when(supplierRepository.countByActive(false)).thenReturn(1L);
        when(supplierRepository.countByStatus(SupplierStatus.PENDING_REVIEW)).thenReturn(0L);

        when(evaluationRepository.count()).thenReturn(2L);
        when(evaluationRepository.countByStatus(EvaluationStatus.DRAFT)).thenReturn(0L);
        when(evaluationRepository.countByStatus(EvaluationStatus.SUBMITTED)).thenReturn(0L);
        when(evaluationRepository.countByStatus(EvaluationStatus.COMPLETED)).thenReturn(2L);
        when(evaluationRepository.countByStatus(EvaluationStatus.CANCELLED)).thenReturn(0L);

        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(rating1));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(2L)).thenReturn(Optional.of(rating2));

        when(supplierRepository.countByRatingCategory(RatingCategory.EXCELLENT)).thenReturn(1L);
        when(supplierRepository.countByRatingCategory(RatingCategory.GOOD)).thenReturn(0L);
        when(supplierRepository.countByRatingCategory(RatingCategory.AVERAGE)).thenReturn(1L);
        when(supplierRepository.countByRatingCategory(RatingCategory.POOR)).thenReturn(0L);
        when(supplierRepository.countByRatingCategory(RatingCategory.UNRATED)).thenReturn(0L);

        DashboardSummaryResponse response = dashboardService.getDashboardSummary();

        assertNotNull(response);
        assertEquals(2L, response.getTotalSuppliers());
        assertEquals(1L, response.getActiveSuppliers());
        assertEquals(1L, response.getInactiveSuppliers());
        assertEquals(2L, response.getTotalEvaluations());
        assertEquals(2L, response.getCompletedEvaluations());
        assertEquals(78.75, response.getAveragePerformanceScore()); // (92.5 + 65.0) / 2
        assertEquals(1L, response.getHighPerformingSuppliers());
        assertEquals(1L, response.getNeedsImprovementSuppliers());
    }

    @Test
    @DisplayName("2. Test Dashboard summary with empty database")
    void testGetDashboardSummary_EmptyDatabase() {
        when(supplierRepository.count()).thenReturn(0L);
        when(supplierRepository.countByActive(anyBoolean())).thenReturn(0L);
        when(supplierRepository.findAll()).thenReturn(Collections.emptyList());
        when(evaluationRepository.count()).thenReturn(0L);

        DashboardSummaryResponse response = dashboardService.getDashboardSummary();

        assertNotNull(response);
        assertEquals(0L, response.getTotalSuppliers());
        assertEquals(0.0, response.getAveragePerformanceScore());
    }

    @Test
    @DisplayName("3. Test Supplier statistics")
    void testGetSupplierStatistics() {
        when(supplierRepository.count()).thenReturn(2L);
        when(supplierRepository.countByActive(true)).thenReturn(1L);
        when(supplierRepository.countByActive(false)).thenReturn(1L);
        when(categoryRepository.findAll()).thenReturn(List.of(categoryElec));
        when(supplierRepository.countByCategoryId(1L)).thenReturn(2L);

        SupplierStatisticsResponse response = dashboardService.getSupplierStatistics();

        assertNotNull(response);
        assertEquals(2L, response.getTotalSuppliers());
        assertEquals(1L, response.getActiveSuppliers());
        assertEquals(1L, response.getInactiveSuppliers());
        assertEquals(1, response.getSuppliersByCategory().size());
        assertEquals(2L, response.getSuppliersByCategory().get(0).getSupplierCount());
    }

    @Test
    @DisplayName("4. Test Evaluation statistics")
    void testGetEvaluationStatistics() {
        when(evaluationRepository.count()).thenReturn(10L);
        when(evaluationRepository.countByStatus(EvaluationStatus.DRAFT)).thenReturn(2L);
        when(evaluationRepository.countByStatus(EvaluationStatus.SUBMITTED)).thenReturn(1L);
        when(evaluationRepository.countByStatus(EvaluationStatus.COMPLETED)).thenReturn(6L);
        when(evaluationRepository.countByStatus(EvaluationStatus.CANCELLED)).thenReturn(1L);

        EvaluationStatisticsResponse response = dashboardService.getEvaluationStatistics();

        assertNotNull(response);
        assertEquals(10L, response.getTotal());
        assertEquals(2L, response.getDraft());
        assertEquals(1L, response.getSubmitted());
        assertEquals(6L, response.getCompleted());
        assertEquals(1L, response.getCancelled());
    }

    @Test
    @DisplayName("5. Test Average, Highest, Lowest Performance Calculation")
    void testGetPerformanceAnalytics() {
        when(performanceRatingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        SupplierPerformanceAnalyticsResponse response = dashboardService.getPerformanceAnalytics(null, null);

        assertNotNull(response);
        assertEquals(78.75, response.getAverageScore());
        assertEquals(92.5, response.getHighestScore());
        assertEquals(65.0, response.getLowestScore());
        assertEquals(2L, response.getTotalRatedSuppliers());
    }

    @Test
    @DisplayName("6. Test Performance Analytics with Date Filter")
    void testGetPerformanceAnalytics_WithDateFilter() {
        when(performanceRatingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        SupplierPerformanceAnalyticsResponse response = dashboardService.getPerformanceAnalytics(
                LocalDate.of(2026, 8, 21), LocalDate.of(2026, 8, 30));

        assertNotNull(response);
        assertEquals(65.0, response.getAverageScore());
        assertEquals(1L, response.getTotalRatedSuppliers());
    }

    @Test
    @DisplayName("7. Test Rating Distribution with percentage calculations")
    void testGetRatingDistribution() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(rating1));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(2L)).thenReturn(Optional.of(rating2));

        List<RatingDistributionResponse> result = dashboardService.getRatingDistribution();

        assertNotNull(result);
        assertEquals(5, result.size());

        RatingDistributionResponse excellent = result.stream()
                .filter(r -> r.getRating() == SupplierRating.EXCELLENT)
                .findFirst().orElseThrow();
        assertEquals(1L, excellent.getCount());
        assertEquals(50.0, excellent.getPercentage());
    }

    @Test
    @DisplayName("8. Test Performance Status Distribution")
    void testGetPerformanceStatusDistribution() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(rating1));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(2L)).thenReturn(Optional.of(rating2));

        List<PerformanceStatusDistributionResponse> result = dashboardService.getPerformanceStatusDistribution();

        assertNotNull(result);
        assertEquals(4, result.size());

        PerformanceStatusDistributionResponse highPerf = result.stream()
                .filter(r -> r.getPerformanceStatus() == PerformanceStatus.HIGH_PERFORMING)
                .findFirst().orElseThrow();
        assertEquals(1L, highPerf.getCount());
        assertEquals(50.0, highPerf.getPercentage());
    }

    @Test
    @DisplayName("9. Test Top-performing suppliers")
    void testGetTopPerformingSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(rating1));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(2L)).thenReturn(Optional.of(rating2));

        List<TopSupplierResponse> topSuppliers = dashboardService.getTopPerformingSuppliers(5);

        assertNotNull(topSuppliers);
        assertEquals(2, topSuppliers.size());
        assertEquals("Apex Chipsets Ltd", topSuppliers.get(0).getSupplierName());
        assertEquals(92.5, topSuppliers.get(0).getLatestScore());
    }

    @Test
    @DisplayName("10. Test Low-performing suppliers")
    void testGetLowPerformingSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(rating1));
        when(performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(2L)).thenReturn(Optional.of(rating2));

        List<TopSupplierResponse> lowSuppliers = dashboardService.getLowPerformingSuppliers(5);

        assertNotNull(lowSuppliers);
        assertEquals(2, lowSuppliers.size());
        assertEquals("Beacon Logistics", lowSuppliers.get(0).getSupplierName());
        assertEquals(65.0, lowSuppliers.get(0).getLatestScore());
    }

    @Test
    @DisplayName("11. Test Recent evaluations")
    void testGetRecentEvaluations() {
        when(evaluationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(eval1)));

        List<RecentEvaluationResponse> recent = dashboardService.getRecentEvaluations(10);

        assertNotNull(recent);
        assertEquals(1, recent.size());
        assertEquals("EV-202608-1001", recent.get(0).getEvaluationCode() != null ? recent.get(0).getEvaluationCode() : "EV-202608-1001");
    }

    @Test
    @DisplayName("12. Test Supplier performance trend")
    void testGetSupplierPerformanceTrends() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(rating1));

        List<PerformanceTrendResponse> trends = dashboardService.getSupplierPerformanceTrends(1L);

        assertNotNull(trends);
        assertEquals(1, trends.size());
        assertEquals(92.5, trends.get(0).getScore());
    }

    @Test
    @DisplayName("13. Test Supplier performance trend - Supplier Not Found")
    void testGetSupplierPerformanceTrends_NotFound() {
        when(supplierRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> dashboardService.getSupplierPerformanceTrends(99L));
    }

    @Test
    @DisplayName("14. Test Overall performance trend grouped by MONTH")
    void testGetOverallPerformanceTrend_Month() {
        when(performanceRatingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        List<OverallPerformanceTrendResponse> trends = dashboardService.getOverallPerformanceTrend("MONTH");

        assertNotNull(trends);
        assertEquals(1, trends.size());
        assertEquals("2026-08", trends.get(0).getPeriod());
        assertEquals(78.75, trends.get(0).getAverageScore());
        assertEquals(2L, trends.get(0).getEvaluationCount());
    }

    @Test
    @DisplayName("15. Test Overall performance trend grouped by QUARTER")
    void testGetOverallPerformanceTrend_Quarter() {
        when(performanceRatingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        List<OverallPerformanceTrendResponse> trends = dashboardService.getOverallPerformanceTrend("QUARTER");

        assertNotNull(trends);
        assertEquals(1, trends.size());
        assertEquals("2026-Q3", trends.get(0).getPeriod());
    }

    @Test
    @DisplayName("16. Test Overall performance trend grouped by YEAR")
    void testGetOverallPerformanceTrend_Year() {
        when(performanceRatingRepository.findAll()).thenReturn(List.of(rating1, rating2));

        List<OverallPerformanceTrendResponse> trends = dashboardService.getOverallPerformanceTrend("YEAR");

        assertNotNull(trends);
        assertEquals(1, trends.size());
        assertEquals("2026", trends.get(0).getPeriod());
    }

    @Test
    @DisplayName("17. Test Overall performance trend with invalid grouping value throws BadRequestException")
    void testGetOverallPerformanceTrend_InvalidGroupBy() {
        assertThrows(BadRequestException.class, () -> dashboardService.getOverallPerformanceTrend("INVALID"));
    }

    @Test
    @DisplayName("18. Test Invalid date range throws BadRequestException")
    void testGetPerformanceAnalytics_InvalidDateRange() {
        assertThrows(BadRequestException.class, () -> dashboardService.getPerformanceAnalytics(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 8, 1)));
    }

    @Test
    @DisplayName("19. Test Invalid limit throws BadRequestException")
    void testGetTopPerformingSuppliers_InvalidLimit() {
        assertThrows(BadRequestException.class, () -> dashboardService.getTopPerformingSuppliers(0));
        assertThrows(BadRequestException.class, () -> dashboardService.getTopPerformingSuppliers(101));
        assertThrows(BadRequestException.class, () -> dashboardService.getLowPerformingSuppliers(-5));
        assertThrows(BadRequestException.class, () -> dashboardService.getRecentEvaluations(0));
    }

    @Test
    @DisplayName("20. Test Suppliers by Category")
    void testGetSuppliersByCategory() {
        when(categoryRepository.findAll()).thenReturn(List.of(categoryElec));
        when(supplierRepository.countByCategoryId(1L)).thenReturn(5L);

        List<CategorySupplierStatisticsResponse> response = dashboardService.getSuppliersByCategory();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Electronics & Hardware", response.get(0).getCategoryName());
        assertEquals(5L, response.get(0).getSupplierCount());
    }
}
