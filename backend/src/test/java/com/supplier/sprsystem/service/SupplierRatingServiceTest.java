package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.RatingHistoryResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceRatingResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceSummaryResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.impl.SupplierRatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierRatingServiceTest {

    @Mock
    private SupplierPerformanceRatingRepository ratingRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierRatingServiceImpl ratingService;

    private Supplier sampleSupplier;
    private SupplierEvaluation sampleCompletedEvaluation;
    private SupplierEvaluation sampleDraftEvaluation;
    private SupplierPerformanceRating sampleRating1;
    private SupplierPerformanceRating sampleRating2;

    @BeforeEach
    void setUp() {
        sampleSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-00001")
                .name("Apex Chipsets Ltd")
                .overallRating(88.0)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(2)
                .build();

        sampleCompletedEvaluation = SupplierEvaluation.builder()
                .id(100L)
                .evaluationCode("EV-202608-1001")
                .supplier(sampleSupplier)
                .evaluationDate(LocalDate.now())
                .status(EvaluationStatus.COMPLETED)
                .totalWeightedScore(92.5)
                .build();

        sampleDraftEvaluation = SupplierEvaluation.builder()
                .id(101L)
                .evaluationCode("EV-202608-1002")
                .supplier(sampleSupplier)
                .evaluationDate(LocalDate.now())
                .status(EvaluationStatus.DRAFT)
                .totalWeightedScore(80.0)
                .build();

        sampleRating1 = SupplierPerformanceRating.builder()
                .id(1L)
                .supplier(sampleSupplier)
                .evaluation(sampleCompletedEvaluation)
                .score(92.5)
                .rating(SupplierRating.EXCELLENT)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .ratingDate(LocalDate.of(2026, 8, 31))
                .build();

        sampleRating2 = SupplierPerformanceRating.builder()
                .id(2L)
                .supplier(sampleSupplier)
                .score(80.0)
                .rating(SupplierRating.VERY_GOOD)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .ratingDate(LocalDate.of(2026, 5, 15))
                .build();
    }

    @Test
    @DisplayName("1. Test EXCELLENT classification (90-100)")
    void testCalculateRating_Excellent() {
        assertEquals(SupplierRating.EXCELLENT, ratingService.calculateRating(95.0));
        assertEquals(SupplierRating.EXCELLENT, ratingService.calculateRating(90.0));
        assertEquals(PerformanceStatus.HIGH_PERFORMING, ratingService.calculatePerformanceStatus(95.0));
    }

    @Test
    @DisplayName("2. Test VERY_GOOD classification (80-89.99)")
    void testCalculateRating_VeryGood() {
        assertEquals(SupplierRating.VERY_GOOD, ratingService.calculateRating(85.0));
        assertEquals(SupplierRating.VERY_GOOD, ratingService.calculateRating(80.0));
        assertEquals(PerformanceStatus.HIGH_PERFORMING, ratingService.calculatePerformanceStatus(85.0));
    }

    @Test
    @DisplayName("3. Test GOOD classification (70-79.99)")
    void testCalculateRating_Good() {
        assertEquals(SupplierRating.GOOD, ratingService.calculateRating(75.0));
        assertEquals(SupplierRating.GOOD, ratingService.calculateRating(70.0));
        assertEquals(PerformanceStatus.SATISFACTORY, ratingService.calculatePerformanceStatus(75.0));
    }

    @Test
    @DisplayName("4. Test AVERAGE classification (60-69.99)")
    void testCalculateRating_Average() {
        assertEquals(SupplierRating.AVERAGE, ratingService.calculateRating(65.0));
        assertEquals(SupplierRating.AVERAGE, ratingService.calculateRating(60.0));
        assertEquals(PerformanceStatus.NEEDS_IMPROVEMENT, ratingService.calculatePerformanceStatus(65.0));
    }

    @Test
    @DisplayName("5. Test POOR classification (0-59.99)")
    void testCalculateRating_Poor() {
        assertEquals(SupplierRating.POOR, ratingService.calculateRating(45.0));
        assertEquals(SupplierRating.POOR, ratingService.calculateRating(0.0));
        assertEquals(PerformanceStatus.LOW_PERFORMING, ratingService.calculatePerformanceStatus(45.0));
    }

    @Test
    @DisplayName("6. Test Score boundary values")
    void testScoreBoundaryValues() {
        assertEquals(SupplierRating.EXCELLENT, SupplierRating.fromScore(100.0));
        assertEquals(SupplierRating.EXCELLENT, SupplierRating.fromScore(90.0));
        assertEquals(SupplierRating.VERY_GOOD, SupplierRating.fromScore(89.99));
        assertEquals(SupplierRating.VERY_GOOD, SupplierRating.fromScore(80.0));
        assertEquals(SupplierRating.GOOD, SupplierRating.fromScore(79.99));
        assertEquals(SupplierRating.GOOD, SupplierRating.fromScore(70.0));
        assertEquals(SupplierRating.AVERAGE, SupplierRating.fromScore(69.99));
        assertEquals(SupplierRating.AVERAGE, SupplierRating.fromScore(60.0));
        assertEquals(SupplierRating.POOR, SupplierRating.fromScore(59.99));
        assertEquals(SupplierRating.POOR, SupplierRating.fromScore(0.0));
    }

    @Test
    @DisplayName("7. Test Score below 0 throws BadRequestException")
    void testCalculateRating_ScoreBelowZero_ThrowsException() {
        assertThrows(BadRequestException.class, () -> ratingService.calculateRating(-1.0));
        assertThrows(BadRequestException.class, () -> ratingService.calculatePerformanceStatus(-0.5));
    }

    @Test
    @DisplayName("8. Test Score above 100 throws BadRequestException")
    void testCalculateRating_ScoreAbove100_ThrowsException() {
        assertThrows(BadRequestException.class, () -> ratingService.calculateRating(105.0));
        assertThrows(BadRequestException.class, () -> ratingService.calculatePerformanceStatus(100.1));
    }

    @Test
    @DisplayName("9. Test Generate Rating from completed evaluation successfully")
    void testGenerateRatingForEvaluation_Completed_Success() {
        when(ratingRepository.findByEvaluationId(100L)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(SupplierPerformanceRating.class))).thenAnswer(inv -> {
            SupplierPerformanceRating r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        SupplierPerformanceRatingResponse response = ratingService.generateRatingForEvaluation(sampleCompletedEvaluation);

        assertNotNull(response);
        assertEquals(92.5, response.getScore());
        assertEquals(SupplierRating.EXCELLENT, response.getRating());
        assertEquals(PerformanceStatus.HIGH_PERFORMING, response.getPerformanceStatus());
        verify(ratingRepository, times(1)).save(any(SupplierPerformanceRating.class));
    }

    @Test
    @DisplayName("10. Test Prevent rating generation from incomplete evaluation (DRAFT throws BadRequestException)")
    void testGenerateRatingForEvaluation_Draft_ThrowsException() {
        assertThrows(BadRequestException.class, () -> ratingService.generateRatingForEvaluation(sampleDraftEvaluation));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("11. Test Prevent duplicate rating generation (idempotent update)")
    void testGenerateRatingForEvaluation_Duplicate_UpdatesExisting() {
        when(ratingRepository.findByEvaluationId(100L)).thenReturn(Optional.of(sampleRating1));
        when(ratingRepository.save(any(SupplierPerformanceRating.class))).thenReturn(sampleRating1);

        sampleCompletedEvaluation.setTotalWeightedScore(95.0);

        SupplierPerformanceRatingResponse response = ratingService.generateRatingForEvaluation(sampleCompletedEvaluation);

        assertNotNull(response);
        assertEquals(95.0, response.getScore());
        verify(ratingRepository, times(1)).save(sampleRating1);
    }

    @Test
    @DisplayName("12. Test Get Latest Supplier Rating")
    void testGetLatestSupplierRating_Success() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(sampleRating1));

        SupplierPerformanceRatingResponse response = ratingService.getLatestSupplierRating(1L);

        assertNotNull(response);
        assertEquals(92.5, response.getScore());
        assertEquals(SupplierRating.EXCELLENT, response.getRating());
    }

    @Test
    @DisplayName("13. Test Get Supplier Rating History")
    void testGetSupplierRatingHistory_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(ratingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(sampleRating1, sampleRating2));

        RatingHistoryResponse response = ratingService.getSupplierRatingHistory(1L);

        assertNotNull(response);
        assertEquals(1L, response.getSupplierId());
        assertEquals(2, response.getRatings().size());
        assertEquals(92.5, response.getRatings().get(0).getScore());
        assertEquals(80.0, response.getRatings().get(1).getScore());
    }

    @Test
    @DisplayName("14. Test Calculate Performance Summary with score difference")
    void testGetSupplierPerformanceSummary_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(ratingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(sampleRating1, sampleRating2));

        SupplierPerformanceSummaryResponse response = ratingService.getSupplierPerformanceSummary(1L);

        assertNotNull(response);
        assertEquals(92.5, response.getLatestScore());
        assertEquals(SupplierRating.EXCELLENT, response.getLatestRating());
        assertEquals(80.0, response.getPreviousScore());
        assertEquals(SupplierRating.VERY_GOOD, response.getPreviousRating());
        assertEquals(12.5, response.getScoreDifference());
        assertEquals(PerformanceTrend.IMPROVING, response.getPerformanceTrend());
        assertEquals(2, response.getTotalEvaluations());
    }

    @Test
    @DisplayName("15. Test IMPROVING Trend (> +2.0)")
    void testPerformanceTrend_Improving() {
        assertEquals(PerformanceTrend.IMPROVING, PerformanceTrend.calculateTrend(85.0, 80.0));
        assertEquals(PerformanceTrend.IMPROVING, PerformanceTrend.calculateTrend(92.1, 90.0));
    }

    @Test
    @DisplayName("16. Test STABLE Trend (between -2.0 and +2.0)")
    void testPerformanceTrend_Stable() {
        assertEquals(PerformanceTrend.STABLE, PerformanceTrend.calculateTrend(85.0, 84.0));
        assertEquals(PerformanceTrend.STABLE, PerformanceTrend.calculateTrend(85.0, 86.5));
        assertEquals(PerformanceTrend.STABLE, PerformanceTrend.calculateTrend(90.0, 90.0));
    }

    @Test
    @DisplayName("17. Test DECLINING Trend (< -2.0)")
    void testPerformanceTrend_Declining() {
        assertEquals(PerformanceTrend.DECLINING, PerformanceTrend.calculateTrend(75.0, 80.0));
        assertEquals(PerformanceTrend.DECLINING, PerformanceTrend.calculateTrend(65.0, 72.0));
    }

    @Test
    @DisplayName("18. Test INSUFFICIENT_DATA Trend (single evaluation or no evaluations)")
    void testPerformanceTrend_InsufficientData() {
        assertEquals(PerformanceTrend.INSUFFICIENT_DATA, PerformanceTrend.calculateTrend(85.0, null));
        assertEquals(PerformanceTrend.INSUFFICIENT_DATA, PerformanceTrend.calculateTrend(null, 80.0));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(ratingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(List.of(sampleRating1));

        SupplierPerformanceSummaryResponse summary = ratingService.getSupplierPerformanceSummary(1L);

        assertNotNull(summary);
        assertEquals(PerformanceTrend.INSUFFICIENT_DATA, summary.getPerformanceTrend());
        assertNull(summary.getPreviousScore());
        assertNull(summary.getScoreDifference());
    }

    @Test
    @DisplayName("19. Test Get High Performing Suppliers")
    void testGetHighPerformingSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(sampleSupplier));
        when(ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(sampleRating1));

        List<SupplierPerformanceRatingResponse> highPerformers = ratingService.getHighPerformingSuppliers();

        assertNotNull(highPerformers);
        assertEquals(1, highPerformers.size());
        assertEquals("Apex Chipsets Ltd", highPerformers.get(0).getSupplierName());
    }

    @Test
    @DisplayName("20. Test Get Suppliers Needing Improvement")
    void testGetSuppliersNeedingImprovement() {
        SupplierPerformanceRating lowRating = SupplierPerformanceRating.builder()
                .id(3L)
                .supplier(sampleSupplier)
                .score(55.0)
                .rating(SupplierRating.POOR)
                .performanceStatus(PerformanceStatus.LOW_PERFORMING)
                .ratingDate(LocalDate.now())
                .build();

        when(supplierRepository.findAll()).thenReturn(List.of(sampleSupplier));
        when(ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(1L)).thenReturn(Optional.of(lowRating));

        List<SupplierPerformanceRatingResponse> lowPerformers = ratingService.getSuppliersNeedingImprovement();

        assertNotNull(lowPerformers);
        assertEquals(1, lowPerformers.size());
        assertEquals(PerformanceStatus.LOW_PERFORMING, lowPerformers.get(0).getPerformanceStatus());
    }
}
