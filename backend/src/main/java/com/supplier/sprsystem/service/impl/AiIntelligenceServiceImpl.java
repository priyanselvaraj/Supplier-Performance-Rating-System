package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.AiIntelligenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AiIntelligenceServiceImpl implements AiIntelligenceService {

    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierPerformanceRatingRepository performanceRatingRepository;
    private final EvaluationCriteriaRepository criteriaRepository;

    @Value("${app.ai.minimum-history:3}")
    private int minimumHistory = 3;

    @Value("${app.ai.high-risk-threshold:50.0}")
    private double highRiskThreshold = 50.0;

    @Value("${app.ai.critical-risk-threshold:75.0}")
    private double criticalRiskThreshold = 75.0;

    @Value("${app.ai.decline-threshold:5.0}")
    private double declineThreshold = 5.0;

    @Value("${app.ai.low-score-threshold:70.0}")
    private double lowScoreThreshold = 70.0;

    public AiIntelligenceServiceImpl(SupplierRepository supplierRepository,
                                    SupplierEvaluationRepository evaluationRepository,
                                    SupplierPerformanceRatingRepository performanceRatingRepository,
                                    EvaluationCriteriaRepository criteriaRepository) {
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.performanceRatingRepository = performanceRatingRepository;
        this.criteriaRepository = criteriaRepository;
    }

    @Override
    public SupplierPredictionResponse getSupplierPrediction(Long supplierId) {
        Supplier supplier = findSupplierOrThrow(supplierId);
        List<SupplierEvaluation> evaluations = getCompletedEvaluationsAsc(supplierId);

        int count = evaluations.size();
        Double currentScore = supplier.getOverallRating() != null ? supplier.getOverallRating() : (count > 0 ? evaluations.get(count - 1).getTotalWeightedScore() : 0.0);
        List<Double> historicalScores = evaluations.stream().map(SupplierEvaluation::getTotalWeightedScore).collect(Collectors.toList());

        if (count < minimumHistory) {
            return SupplierPredictionResponse.builder()
                    .supplierId(supplier.getId())
                    .supplierName(supplier.getName())
                    .supplierCode(supplier.getSupplierCode())
                    .currentScore(round(currentScore, 1))
                    .predictedScore(null)
                    .confidence(ConfidenceLevel.LOW)
                    .trend(PerformanceTrend.INSUFFICIENT_DATA)
                    .velocityRate(0.0)
                    .historicalEvaluationsCount(count)
                    .historicalScores(historicalScores)
                    .predictionMethod("Insufficient Historical Records")
                    .explanation("Prediction requires at least " + minimumHistory + " completed evaluation cycles to establish a statistically meaningful baseline.")
                    .sufficientData(false)
                    .statusMessage("Insufficient historical data for prediction (minimum " + minimumHistory + " required).")
                    .build();
        }

        // Weighted Linear Regression: recent evaluations have higher weights
        double sumWeights = 0.0;
        double sumWeightedX = 0.0;
        double sumWeightedY = 0.0;

        for (int i = 0; i < count; i++) {
            double w = i + 1.0; // Weight increases with recency
            double x = i + 1.0;
            double y = historicalScores.get(i);

            sumWeights += w;
            sumWeightedX += w * x;
            sumWeightedY += w * y;
        }

        double xBar = sumWeightedX / sumWeights;
        double yBar = sumWeightedY / sumWeights;

        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < count; i++) {
            double w = i + 1.0;
            double x = i + 1.0;
            double y = historicalScores.get(i);

            numerator += w * (x - xBar) * (y - yBar);
            denominator += w * Math.pow(x - xBar, 2);
        }

        double slope = denominator != 0 ? numerator / denominator : 0.0;
        double predicted = currentScore + slope;
        predicted = Math.max(0.0, Math.min(100.0, predicted));

        // Determine trend
        PerformanceTrend trend;
        if (slope > 1.0) {
            trend = PerformanceTrend.IMPROVING;
        } else if (slope < -1.0) {
            trend = PerformanceTrend.DECLINING;
        } else {
            trend = PerformanceTrend.STABLE;
        }

        // Determine confidence
        ConfidenceLevel confidence;
        if (count >= 5) {
            confidence = ConfidenceLevel.HIGH;
        } else if (count >= minimumHistory) {
            confidence = ConfidenceLevel.MEDIUM;
        } else {
            confidence = ConfidenceLevel.LOW;
        }

        // Generate explainable statement
        String explanation;
        if (trend == PerformanceTrend.DECLINING) {
            explanation = String.format("Predicted score of %.1f%% (projected drop of %.1f pts) based on %d historical evaluations with a negative velocity of %.2f pts/cycle.",
                    predicted, Math.abs(predicted - currentScore), count, slope);
        } else if (trend == PerformanceTrend.IMPROVING) {
            explanation = String.format("Predicted score of %.1f%% (projected gain of +%.1f pts) based on %d historical evaluations with an upward velocity of +%.2f pts/cycle.",
                    predicted, (predicted - currentScore), count, slope);
        } else {
            explanation = String.format("Predicted score of %.1f%% indicating stable consistency across %d historical evaluation cycles.",
                    predicted, count);
        }

        return SupplierPredictionResponse.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .supplierCode(supplier.getSupplierCode())
                .currentScore(round(currentScore, 1))
                .predictedScore(round(predicted, 1))
                .confidence(confidence)
                .trend(trend)
                .velocityRate(round(slope, 2))
                .historicalEvaluationsCount(count)
                .historicalScores(historicalScores)
                .predictionMethod("Time-Weighted Linear Trajectory Regression")
                .explanation(explanation)
                .sufficientData(true)
                .statusMessage("Prediction successfully generated.")
                .build();
    }

    @Override
    public SupplierRiskResponse getSupplierRisk(Long supplierId) {
        Supplier supplier = findSupplierOrThrow(supplierId);
        List<SupplierEvaluation> evaluations = getCompletedEvaluationsAsc(supplierId);
        int count = evaluations.size();

        Double currentScore = supplier.getOverallRating() != null ? supplier.getOverallRating() : (count > 0 ? evaluations.get(count - 1).getTotalWeightedScore() : 0.0);
        List<String> riskFactors = new ArrayList<>();

        // Base risk score (up to 50 points from score deficit)
        double scoreDeficit = Math.max(0.0, 100.0 - currentScore);
        double riskScore = scoreDeficit * 0.50;

        if (currentScore < lowScoreThreshold) {
            riskFactors.add(String.format("Overall score (%.1f%%) is below the minimum threshold of %.1f%%.", currentScore, lowScoreThreshold));
        }

        PerformanceTrend trend = PerformanceTrend.INSUFFICIENT_DATA;
        if (count >= 2) {
            double latestScore = evaluations.get(count - 1).getTotalWeightedScore();
            double prevScore = evaluations.get(count - 2).getTotalWeightedScore();
            double drop = prevScore - latestScore;

            if (drop >= declineThreshold) {
                riskScore += 15.0;
                riskFactors.add(String.format("Performance decreased sharply by %.1f points between the last two evaluation cycles.", drop));
            }

            if (latestScore > prevScore + 2.0) {
                trend = PerformanceTrend.IMPROVING;
                riskScore -= 10.0;
            } else if (latestScore < prevScore - 2.0) {
                trend = PerformanceTrend.DECLINING;
                riskScore += 15.0;
                riskFactors.add("Consecutive performance evaluations show a declining trajectory.");
            } else {
                trend = PerformanceTrend.STABLE;
            }
        } else {
            riskScore += 10.0;
            riskFactors.add("Limited evaluation history (< 2 cycles) introduces operational uncertainty.");
        }

        // Criterion-specific deficiencies in latest evaluation
        if (count > 0) {
            SupplierEvaluation latest = evaluations.get(count - 1);
            for (EvaluationScore s : latest.getScores()) {
                double pct = (s.getScoreObtained() / s.getMaxScore()) * 100.0;
                if (pct < 60.0) {
                    riskScore += 10.0;
                    riskFactors.add(String.format("Severe deficiency detected in %s (scored %.1f%%).", s.getCriteria().getName(), pct));
                    break;
                }
            }
        }

        riskScore = Math.max(0.0, Math.min(100.0, riskScore));

        RiskLevel riskLevel;
        if (riskScore >= criticalRiskThreshold) {
            riskLevel = RiskLevel.CRITICAL;
        } else if (riskScore >= highRiskThreshold) {
            riskLevel = RiskLevel.HIGH;
        } else if (riskScore >= 25.0) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.LOW;
        }

        if (riskFactors.isEmpty()) {
            riskFactors.add("No significant operational or compliance risk factors detected.");
        }

        String explanation = String.format("Supplier assigned %s risk (Score: %.1f/100) based on %d risk factor indicator(s).",
                riskLevel.getDisplayName(), riskScore, riskFactors.size());

        List<AiAlertResponse> alerts = getSupplierAlerts(supplierId);

        return SupplierRiskResponse.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .supplierCode(supplier.getSupplierCode())
                .currentScore(round(currentScore, 1))
                .ratingCategory(supplier.getRatingCategory())
                .trend(trend)
                .riskScore(round(riskScore, 1))
                .riskLevel(riskLevel)
                .riskFactors(riskFactors)
                .explanation(explanation)
                .activeAlertsCount(alerts.size())
                .build();
    }

    @Override
    public SupplierAiTrendResponse getSupplierTrend(Long supplierId) {
        Supplier supplier = findSupplierOrThrow(supplierId);
        List<SupplierEvaluation> evaluations = getCompletedEvaluationsAsc(supplierId);
        int count = evaluations.size();

        if (count < 2) {
            Double currentScore = supplier.getOverallRating() != null ? supplier.getOverallRating() : 0.0;
            return SupplierAiTrendResponse.builder()
                    .supplierId(supplier.getId())
                    .supplierName(supplier.getName())
                    .trend(PerformanceTrend.INSUFFICIENT_DATA)
                    .previousScore(null)
                    .currentScore(round(currentScore, 1))
                    .scoreDifference(0.0)
                    .percentageChange(0.0)
                    .recentScores(evaluations.stream().map(SupplierEvaluation::getTotalWeightedScore).collect(Collectors.toList()))
                    .evaluationPeriods(evaluations.stream().map(e -> e.getEvaluationPeriod() != null ? e.getEvaluationPeriod() : e.getEvaluationDate().toString()).collect(Collectors.toList()))
                    .explanation("Insufficient historical evaluations to establish a multi-point trend analysis.")
                    .build();
        }

        double latestScore = evaluations.get(count - 1).getTotalWeightedScore();
        double prevScore = evaluations.get(count - 2).getTotalWeightedScore();
        double diff = latestScore - prevScore;
        double pctChange = prevScore != 0 ? (diff / prevScore) * 100.0 : 0.0;

        PerformanceTrend trend;
        if (diff > 2.0) {
            trend = PerformanceTrend.IMPROVING;
        } else if (diff < -2.0) {
            trend = PerformanceTrend.DECLINING;
        } else {
            trend = PerformanceTrend.STABLE;
        }

        String explanation;
        if (trend == PerformanceTrend.IMPROVING) {
            explanation = String.format("Supplier score improved by +%.1f points (+%.1f%%) from %.1f to %.1f.", diff, pctChange, prevScore, latestScore);
        } else if (trend == PerformanceTrend.DECLINING) {
            explanation = String.format("Supplier score declined by %.1f points (%.1f%%) from %.1f to %.1f.", Math.abs(diff), pctChange, prevScore, latestScore);
        } else {
            explanation = String.format("Supplier score remained stable at %.1f (variance of %.1f points from previous %.1f).", latestScore, diff, prevScore);
        }

        return SupplierAiTrendResponse.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .trend(trend)
                .previousScore(round(prevScore, 1))
                .currentScore(round(latestScore, 1))
                .scoreDifference(round(diff, 1))
                .percentageChange(round(pctChange, 1))
                .recentScores(evaluations.stream().map(SupplierEvaluation::getTotalWeightedScore).collect(Collectors.toList()))
                .evaluationPeriods(evaluations.stream().map(e -> e.getEvaluationPeriod() != null ? e.getEvaluationPeriod() : e.getEvaluationDate().toString()).collect(Collectors.toList()))
                .explanation(explanation)
                .build();
    }

    @Override
    public List<AiRecommendationResponse> getSupplierRecommendations(Long supplierId) {
        Supplier supplier = findSupplierOrThrow(supplierId);
        List<SupplierEvaluation> evaluations = getCompletedEvaluationsAsc(supplierId);

        List<AiRecommendationResponse> recommendations = new ArrayList<>();
        if (evaluations.isEmpty()) {
            recommendations.add(AiRecommendationResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .supplierId(supplier.getId())
                    .criterionName("Initial Evaluation Required")
                    .currentScore(0.0)
                    .maxScore(100.0)
                    .percentage(0.0)
                    .priority(RecommendationPriority.HIGH)
                    .title("Schedule Baseline Evaluation")
                    .recommendation("No completed performance evaluations found for this supplier.")
                    .actionableSteps("Schedule and complete an initial scorecard evaluation to establish baseline KPIs.")
                    .build());
            return recommendations;
        }

        SupplierEvaluation latest = evaluations.get(evaluations.size() - 1);

        for (EvaluationScore s : latest.getScores()) {
            double max = s.getMaxScore() != null && s.getMaxScore() > 0 ? s.getMaxScore() : 100.0;
            double obtained = s.getScoreObtained();
            double percentage = (obtained / max) * 100.0;

            if (percentage < 75.0) {
                RecommendationPriority priority;
                if (percentage < 50.0) {
                    priority = RecommendationPriority.CRITICAL;
                } else if (percentage < 65.0) {
                    priority = RecommendationPriority.HIGH;
                } else {
                    priority = RecommendationPriority.MEDIUM;
                }

                String criterionName = s.getCriteria() != null ? s.getCriteria().getName() : "General";
                String nameLower = criterionName.toLowerCase();

                String title;
                String recText;
                String actionText;

                if (nameLower.contains("qual")) {
                    title = "Address Product/Service Quality Deficiencies";
                    recText = String.format("Quality score of %.1f%% is below standard. Elevated defect rates or non-conformances threaten operational compliance.", percentage);
                    actionText = "Initiate Joint Quality Audit, mandate ISO-9001 compliance verification, and enforce pre-shipment inspection gates.";
                } else if (nameLower.contains("deliv") || nameLower.contains("logis")) {
                    title = "Remediate Delivery Lead Times & SLA Punctuality";
                    recText = String.format("Delivery score of %.1f%% indicates shipping delays or order inaccuracies.", percentage);
                    actionText = "Establish automated freight tracking, introduce liquidated delay penalties in master agreement, and review buffer inventory levels.";
                } else if (nameLower.contains("price") || nameLower.contains("cost")) {
                    title = "Benchmarking & Cost Competitiveness Review";
                    recText = String.format("Pricing competitiveness score of %.1f%% is suboptimal relative to category averages.", percentage);
                    actionText = "Conduct market index benchmarking, renegotiate volume tiered rebate schedules, and audit ancillary invoice surcharges.";
                } else if (nameLower.contains("serv") || nameLower.contains("supp")) {
                    title = "Enhance Account Management & Service Responsiveness";
                    recText = String.format("Service rating of %.1f%% reflects slow issue resolution or communication bottlenecks.", percentage);
                    actionText = "Schedule bi-weekly account governance reviews and establish dedicated Tier-1 SLA response channels.";
                } else {
                    title = String.format("Improve Performance in %s", criterionName);
                    recText = String.format("Criterion %s scored %.1f%%, representing an area for operational remediation.", criterionName, percentage);
                    actionText = String.format("Work with vendor account executives on a Corrective Action Plan (CAP) targeting %s.", criterionName);
                }

                recommendations.add(AiRecommendationResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .supplierId(supplier.getId())
                        .criterionName(criterionName)
                        .currentScore(round(obtained, 1))
                        .maxScore(round(max, 1))
                        .percentage(round(percentage, 1))
                        .priority(priority)
                        .title(title)
                        .recommendation(recText)
                        .actionableSteps(actionText)
                        .build());
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add(AiRecommendationResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .supplierId(supplier.getId())
                    .criterionName("Overall Excellence")
                    .currentScore(round(latest.getTotalWeightedScore(), 1))
                    .maxScore(100.0)
                    .percentage(round(latest.getTotalWeightedScore(), 1))
                    .priority(RecommendationPriority.LOW)
                    .title("Maintain High Performance Standards")
                    .recommendation("Supplier meets or exceeds performance targets across all evaluated criteria.")
                    .actionableSteps("Maintain standard quarterly review intervals and explore preferred vendor tier incentives.")
                    .build());
        }

        // Sort by priority critical -> high -> medium -> low
        recommendations.sort(Comparator.comparing(AiRecommendationResponse::getPriority));
        return recommendations;
    }

    @Override
    public List<AiAlertResponse> getSupplierAlerts(Long supplierId) {
        Supplier supplier = findSupplierOrThrow(supplierId);
        List<SupplierEvaluation> evaluations = getCompletedEvaluationsAsc(supplierId);
        int count = evaluations.size();

        List<AiAlertResponse> alerts = new ArrayList<>();
        Double currentScore = supplier.getOverallRating() != null ? supplier.getOverallRating() : (count > 0 ? evaluations.get(count - 1).getTotalWeightedScore() : 0.0);

        // Alert 1: Low Performance
        if (currentScore > 0 && currentScore < lowScoreThreshold) {
            AlertSeverity severity = currentScore < 50.0 ? AlertSeverity.CRITICAL : AlertSeverity.HIGH;
            alerts.add(AiAlertResponse.builder()
                    .id("ALT-" + supplier.getId() + "-LOW")
                    .supplierId(supplier.getId())
                    .supplierName(supplier.getName())
                    .supplierCode(supplier.getSupplierCode())
                    .alertType(AlertType.LOW_PERFORMANCE)
                    .severity(severity)
                    .title("Substandard Overall Performance")
                    .message(String.format("Supplier overall score (%.1f%%) has breached minimum operational threshold (%.1f%%).", currentScore, lowScoreThreshold))
                    .triggeredDate(LocalDate.now())
                    .status(AlertStatus.ACTIVE)
                    .build());
        }

        // Alert 2: Performance Drop
        if (count >= 2) {
            double latestScore = evaluations.get(count - 1).getTotalWeightedScore();
            double prevScore = evaluations.get(count - 2).getTotalWeightedScore();
            double drop = prevScore - latestScore;

            if (drop >= declineThreshold) {
                AlertSeverity severity = drop >= 15.0 ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
                alerts.add(AiAlertResponse.builder()
                        .id("ALT-" + supplier.getId() + "-DROP")
                        .supplierId(supplier.getId())
                        .supplierName(supplier.getName())
                        .supplierCode(supplier.getSupplierCode())
                        .alertType(AlertType.PERFORMANCE_DECLINE)
                        .severity(severity)
                        .title("Significant Performance Decline")
                        .message(String.format("Supplier experienced a drop of %.1f points from previous evaluation (%.1f%% to %.1f%%).", drop, prevScore, latestScore))
                        .triggeredDate(evaluations.get(count - 1).getEvaluationDate())
                        .status(AlertStatus.ACTIVE)
                        .build());
            }
        }

        // Alert 3: Repeated Poor Performance
        if (count >= 2) {
            double s1 = evaluations.get(count - 1).getTotalWeightedScore();
            double s2 = evaluations.get(count - 2).getTotalWeightedScore();
            if (s1 < 70.0 && s2 < 70.0) {
                alerts.add(AiAlertResponse.builder()
                        .id("ALT-" + supplier.getId() + "-REPEAT")
                        .supplierId(supplier.getId())
                        .supplierName(supplier.getName())
                        .supplierCode(supplier.getSupplierCode())
                        .alertType(AlertType.REPEATED_POOR_PERFORMANCE)
                        .severity(AlertSeverity.CRITICAL)
                        .title("Repeated Substandard Evaluations")
                        .message("Supplier has registered consecutive evaluation cycles in POOR or AVERAGE rating tiers.")
                        .triggeredDate(evaluations.get(count - 1).getEvaluationDate())
                        .status(AlertStatus.ACTIVE)
                        .build());
            }
        }

        return alerts;
    }

    @Override
    public List<AiAlertResponse> getAllActiveAlerts() {
        List<Supplier> suppliers = supplierRepository.findAll();
        List<AiAlertResponse> allAlerts = new ArrayList<>();

        for (Supplier s : suppliers) {
            allAlerts.addAll(getSupplierAlerts(s.getId()));
        }

        // Sort by severity (CRITICAL first, then HIGH, WARNING, INFO)
        allAlerts.sort(Comparator.comparing(AiAlertResponse::getSeverity));
        return allAlerts;
    }

    @Override
    public SupplierAiInsightsResponse getSupplierInsights(Long supplierId) {
        SupplierPredictionResponse prediction = getSupplierPrediction(supplierId);
        SupplierRiskResponse risk = getSupplierRisk(supplierId);
        SupplierAiTrendResponse trend = getSupplierTrend(supplierId);
        List<AiRecommendationResponse> recommendations = getSupplierRecommendations(supplierId);
        List<AiAlertResponse> alerts = getSupplierAlerts(supplierId);

        return SupplierAiInsightsResponse.builder()
                .prediction(prediction)
                .risk(risk)
                .trend(trend)
                .recommendations(recommendations)
                .alerts(alerts)
                .build();
    }

    @Override
    public AiDashboardResponse getAiDashboardSummary() {
        List<Supplier> suppliers = supplierRepository.findAll();
        int total = suppliers.size();

        Map<RiskLevel, Long> riskDistribution = new EnumMap<>(RiskLevel.class);
        for (RiskLevel level : RiskLevel.values()) {
            riskDistribution.put(level, 0L);
        }

        List<SupplierRiskResponse> riskList = new ArrayList<>();
        List<SupplierPredictionResponse> decliningPredictions = new ArrayList<>();
        List<AiRecommendationResponse> topRecommendations = new ArrayList<>();
        double totalRiskScore = 0.0;
        int decliningCount = 0;

        for (Supplier s : suppliers) {
            SupplierRiskResponse risk = getSupplierRisk(s.getId());
            riskList.add(risk);
            totalRiskScore += risk.getRiskScore();
            riskDistribution.put(risk.getRiskLevel(), riskDistribution.get(risk.getRiskLevel()) + 1);

            SupplierPredictionResponse pred = getSupplierPrediction(s.getId());
            if (pred.getTrend() == PerformanceTrend.DECLINING) {
                decliningCount++;
                decliningPredictions.add(pred);
            }

            topRecommendations.addAll(getSupplierRecommendations(s.getId()));
        }

        List<AiAlertResponse> allAlerts = getAllActiveAlerts();
        List<AiAlertResponse> criticalAlerts = allAlerts.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL || a.getSeverity() == AlertSeverity.HIGH)
                .limit(10)
                .collect(Collectors.toList());

        // Sort riskList descending by risk score
        riskList.sort((a, b) -> Double.compare(b.getRiskScore(), a.getRiskScore()));
        List<SupplierRiskResponse> topRiskSuppliers = riskList.stream().limit(8).collect(Collectors.toList());

        // Filter high priority recommendations
        topRecommendations = topRecommendations.stream()
                .filter(r -> r.getPriority() == RecommendationPriority.CRITICAL || r.getPriority() == RecommendationPriority.HIGH)
                .limit(8)
                .collect(Collectors.toList());

        double avgRisk = total > 0 ? totalRiskScore / total : 0.0;

        return AiDashboardResponse.builder()
                .totalSuppliersAnalyzed(total)
                .lowRiskCount(riskDistribution.get(RiskLevel.LOW).intValue())
                .mediumRiskCount(riskDistribution.get(RiskLevel.MEDIUM).intValue())
                .highRiskCount(riskDistribution.get(RiskLevel.HIGH).intValue())
                .criticalRiskCount(riskDistribution.get(RiskLevel.CRITICAL).intValue())
                .decliningSuppliersCount(decliningCount)
                .activeAlertsCount(allAlerts.size())
                .averageSystemRiskScore(round(avgRisk, 1))
                .riskDistribution(riskDistribution)
                .criticalAlerts(criticalAlerts)
                .topRiskSuppliers(topRiskSuppliers)
                .decliningPredictions(decliningPredictions)
                .topRecommendations(topRecommendations)
                .build();
    }

    private Supplier findSupplierOrThrow(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
    }

    private List<SupplierEvaluation> getCompletedEvaluationsAsc(Long supplierId) {
        return evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(supplierId, EvaluationStatus.COMPLETED);
    }

    private double round(double val, int places) {
        if (Double.isNaN(val) || Double.isInfinite(val)) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(val);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
