package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.impl.AiIntelligenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiIntelligenceServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierPerformanceRatingRepository performanceRatingRepository;

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @InjectMocks
    private AiIntelligenceServiceImpl aiService;

    private Supplier mockSupplier;
    private EvaluationCriteria qualityCriteria;
    private EvaluationCriteria deliveryCriteria;

    @BeforeEach
    void setUp() {
        mockSupplier = Supplier.builder()
                .id(1L)
                .name("Acme Chipsets")
                .supplierCode("SUP-00001")
                .email("acme@chips.com")
                .overallRating(85.0)
                .ratingCategory(RatingCategory.EXCELLENT)
                .status(SupplierStatus.ACTIVE)
                .build();

        qualityCriteria = EvaluationCriteria.builder()
                .id(1L)
                .name("Product Quality")
                .code("CRIT-QUAL")
                .weight(50.0)
                .maxScore(100.0)
                .active(true)
                .build();

        deliveryCriteria = EvaluationCriteria.builder()
                .id(2L)
                .name("Delivery Performance")
                .code("CRIT-DELV")
                .weight(50.0)
                .maxScore(100.0)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("AI-01: Prediction returns insufficient data status when fewer than 3 evaluations exist")
    void testPredictionInsufficientData() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 80.0, LocalDate.of(2026, 1, 15)),
                        createEvaluation(2L, 85.0, LocalDate.of(2026, 4, 15))
                ));

        SupplierPredictionResponse res = aiService.getSupplierPrediction(1L);

        assertThat(res).isNotNull();
        assertThat(res.isSufficientData()).isFalse();
        assertThat(res.getPredictedScore()).isNull();
        assertThat(res.getStatusMessage()).contains("Insufficient historical data");
        assertThat(res.getHistoricalEvaluationsCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("AI-02: Prediction correctly detects declining trajectory with statistical slope")
    void testPredictionDecliningTrend() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 92.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 85.0, LocalDate.of(2025, 9, 15)),
                        createEvaluation(3L, 78.0, LocalDate.of(2025, 12, 15))
                ));

        SupplierPredictionResponse res = aiService.getSupplierPrediction(1L);

        assertThat(res).isNotNull();
        assertThat(res.isSufficientData()).isTrue();
        assertThat(res.getPredictedScore()).isNotNull();
        assertThat(res.getPredictedScore()).isLessThan(85.0);
        assertThat(res.getTrend()).isEqualTo(PerformanceTrend.DECLINING);
        assertThat(res.getExplanation()).contains("projected drop");
    }

    @Test
    @DisplayName("AI-03: Prediction correctly detects improving trajectory")
    void testPredictionImprovingTrend() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 70.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 80.0, LocalDate.of(2025, 9, 15)),
                        createEvaluation(3L, 90.0, LocalDate.of(2025, 12, 15))
                ));

        SupplierPredictionResponse res = aiService.getSupplierPrediction(1L);

        assertThat(res).isNotNull();
        assertThat(res.isSufficientData()).isTrue();
        assertThat(res.getPredictedScore()).isGreaterThan(85.0);
        assertThat(res.getTrend()).isEqualTo(PerformanceTrend.IMPROVING);
    }

    @Test
    @DisplayName("AI-04: Risk calculation computes LOW risk for high-performing stable supplier")
    void testRiskLow() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 90.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 92.0, LocalDate.of(2025, 9, 15))
                ));

        SupplierRiskResponse risk = aiService.getSupplierRisk(1L);

        assertThat(risk).isNotNull();
        assertThat(risk.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(risk.getRiskScore()).isLessThan(25.0);
    }

    @Test
    @DisplayName("AI-05: Risk calculation identifies HIGH/CRITICAL risk on sharp drop and substandard scores")
    void testRiskHigh() {
        mockSupplier.setOverallRating(55.0);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 85.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 55.0, LocalDate.of(2025, 9, 15))
                ));

        SupplierRiskResponse risk = aiService.getSupplierRisk(1L);

        assertThat(risk).isNotNull();
        assertThat(risk.getRiskLevel()).isIn(RiskLevel.HIGH, RiskLevel.CRITICAL);
        assertThat(risk.getRiskFactors()).anyMatch(f -> f.contains("decreased sharply"));
    }

    @Test
    @DisplayName("AI-06: Trend analysis computes score difference and percentage change")
    void testTrendAnalysis() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 75.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 85.0, LocalDate.of(2025, 9, 15))
                ));

        SupplierAiTrendResponse trend = aiService.getSupplierTrend(1L);

        assertThat(trend).isNotNull();
        assertThat(trend.getTrend()).isEqualTo(PerformanceTrend.IMPROVING);
        assertThat(trend.getScoreDifference()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("AI-07: Recommendations engine detects criteria deficiencies (<75%) and assigns priority")
    void testRecommendationsDeficiency() {
        SupplierEvaluation eval = createEvaluation(1L, 65.0, LocalDate.now());
        EvaluationScore score1 = EvaluationScore.builder()
                .criteria(qualityCriteria)
                .scoreObtained(45.0)
                .maxScore(100.0)
                .weight(50.0)
                .weightedScore(22.5)
                .build();
        EvaluationScore score2 = EvaluationScore.builder()
                .criteria(deliveryCriteria)
                .scoreObtained(85.0)
                .maxScore(100.0)
                .weight(50.0)
                .weightedScore(42.5)
                .build();
        eval.setScores(List.of(score1, score2));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(eval));

        List<AiRecommendationResponse> recs = aiService.getSupplierRecommendations(1L);

        assertThat(recs).isNotEmpty();
        assertThat(recs).anyMatch(r -> r.getCriterionName().contains("Quality") && r.getPriority() == RecommendationPriority.CRITICAL);
    }

    @Test
    @DisplayName("AI-08: Early warning system triggers alerts on performance drops")
    void testAlertsGeneration() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 85.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 65.0, LocalDate.of(2025, 9, 15))
                ));

        List<AiAlertResponse> alerts = aiService.getSupplierAlerts(1L);

        assertThat(alerts).isNotEmpty();
        assertThat(alerts).anyMatch(a -> a.getAlertType() == AlertType.PERFORMANCE_DECLINE);
    }

    @Test
    @DisplayName("AI-09: AI Dashboard aggregates system risk distribution and counts")
    void testAiDashboardSummary() {
        when(supplierRepository.findAll()).thenReturn(List.of(mockSupplier));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(1L, EvaluationStatus.COMPLETED))
                .thenReturn(List.of(
                        createEvaluation(1L, 85.0, LocalDate.of(2025, 6, 15)),
                        createEvaluation(2L, 90.0, LocalDate.of(2025, 9, 15)),
                        createEvaluation(3L, 92.0, LocalDate.of(2025, 12, 15))
                ));

        AiDashboardResponse dashboard = aiService.getAiDashboardSummary();

        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getTotalSuppliersAnalyzed()).isEqualTo(1);
        assertThat(dashboard.getRiskDistribution()).containsKey(RiskLevel.LOW);
    }

    private SupplierEvaluation createEvaluation(Long id, Double score, LocalDate date) {
        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setId(id);
        eval.setSupplier(mockSupplier);
        eval.setEvaluationCode("EV-TEST-" + id);
        eval.setTotalWeightedScore(score);
        eval.setEvaluationDate(date);
        eval.setStatus(EvaluationStatus.COMPLETED);
        eval.setScores(new ArrayList<>());
        return eval;
    }
}
