package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.KpiDefinitionRequest;
import com.supplier.sprsystem.dto.response.KpiCalculationResultResponse;
import com.supplier.sprsystem.dto.response.KpiDefinitionResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.KpiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class KpiServiceImpl implements KpiService {

    private static final Logger log = LoggerFactory.getLogger(KpiServiceImpl.class);

    private final KpiDefinitionRepository kpiDefinitionRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierImprovementActionRepository improvementActionRepository;
    private final ApprovalTaskRepository approvalTaskRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final EvaluationCriteriaRepository criteriaRepository;

    public KpiServiceImpl(
            KpiDefinitionRepository kpiDefinitionRepository,
            SupplierRepository supplierRepository,
            SupplierEvaluationRepository evaluationRepository,
            SupplierImprovementActionRepository improvementActionRepository,
            ApprovalTaskRepository approvalTaskRepository,
            WorkflowInstanceRepository workflowInstanceRepository,
            EvaluationCriteriaRepository criteriaRepository) {
        this.kpiDefinitionRepository = kpiDefinitionRepository;
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.improvementActionRepository = improvementActionRepository;
        this.approvalTaskRepository = approvalTaskRepository;
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.criteriaRepository = criteriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiDefinitionResponse> getAllKpiDefinitions() {
        return kpiDefinitionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiDefinitionResponse> getActiveKpiDefinitions() {
        return kpiDefinitionRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KpiDefinitionResponse getKpiDefinitionById(Long id) {
        KpiDefinition kpi = kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found with id: " + id));
        return mapToResponse(kpi);
    }

    @Override
    public KpiDefinitionResponse createKpiDefinition(KpiDefinitionRequest request) {
        if (kpiDefinitionRepository.existsByKpiCode(request.getKpiCode().toUpperCase().trim())) {
            throw new BadRequestException("KPI definition with code '" + request.getKpiCode() + "' already exists.");
        }

        validateThresholds(request.getWarningThreshold(), request.getCriticalThreshold(),
                request.getTargetValue(), request.getHigherIsBetter());

        KpiDefinition kpi = KpiDefinition.builder()
                .kpiCode(request.getKpiCode().toUpperCase().trim())
                .name(request.getName().trim())
                .description(request.getDescription())
                .category(request.getCategory())
                .calculationType(request.getCalculationType())
                .unit(request.getUnit() != null ? request.getUnit().trim() : "POINTS")
                .targetValue(request.getTargetValue())
                .warningThreshold(request.getWarningThreshold())
                .criticalThreshold(request.getCriticalThreshold())
                .higherIsBetter(request.getHigherIsBetter() != null ? request.getHigherIsBetter() : true)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        KpiDefinition saved = kpiDefinitionRepository.save(kpi);
        log.info("Created KPI definition: {} ({})", saved.getName(), saved.getKpiCode());
        return mapToResponse(saved);
    }

    @Override
    public KpiDefinitionResponse updateKpiDefinition(Long id, KpiDefinitionRequest request) {
        KpiDefinition kpi = kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found with id: " + id));

        String newCode = request.getKpiCode().toUpperCase().trim();
        if (!kpi.getKpiCode().equalsIgnoreCase(newCode) && kpiDefinitionRepository.existsByKpiCode(newCode)) {
            throw new BadRequestException("KPI definition with code '" + newCode + "' already exists.");
        }

        validateThresholds(request.getWarningThreshold(), request.getCriticalThreshold(),
                request.getTargetValue(), request.getHigherIsBetter());

        kpi.setKpiCode(newCode);
        kpi.setName(request.getName().trim());
        kpi.setDescription(request.getDescription());
        kpi.setCategory(request.getCategory());
        kpi.setCalculationType(request.getCalculationType());
        if (request.getUnit() != null) kpi.setUnit(request.getUnit().trim());
        kpi.setTargetValue(request.getTargetValue());
        kpi.setWarningThreshold(request.getWarningThreshold());
        kpi.setCriticalThreshold(request.getCriticalThreshold());
        if (request.getHigherIsBetter() != null) kpi.setHigherIsBetter(request.getHigherIsBetter());
        if (request.getActive() != null) kpi.setActive(request.getActive());

        KpiDefinition updated = kpiDefinitionRepository.save(kpi);
        log.info("Updated KPI definition: {} ({})", updated.getName(), updated.getKpiCode());
        return mapToResponse(updated);
    }

    @Override
    public KpiDefinitionResponse toggleKpiDefinition(Long id) {
        KpiDefinition kpi = kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found with id: " + id));
        kpi.setActive(!kpi.getActive());
        KpiDefinition updated = kpiDefinitionRepository.save(kpi);
        log.info("Toggled KPI definition active status: {} -> {}", updated.getKpiCode(), updated.getActive());
        return mapToResponse(updated);
    }

    @Override
    public void deleteKpiDefinition(Long id) {
        KpiDefinition kpi = kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found with id: " + id));
        kpiDefinitionRepository.delete(kpi);
        log.info("Deleted KPI definition: {} ({})", kpi.getName(), kpi.getKpiCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiCalculationResultResponse> calculateAllActiveKpis() {
        List<KpiDefinition> activeKpis = kpiDefinitionRepository.findByActiveTrue();
        List<KpiCalculationResultResponse> results = new ArrayList<>();
        for (KpiDefinition kpi : activeKpis) {
            results.add(calculateKpiInternal(kpi));
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public KpiCalculationResultResponse calculateKpi(String kpiCode) {
        KpiDefinition kpi = kpiDefinitionRepository.findByKpiCode(kpiCode.toUpperCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found for code: " + kpiCode));
        return calculateKpiInternal(kpi);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiCalculationResultResponse calculateKpiById(Long id) {
        KpiDefinition kpi = kpiDefinitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI definition not found with id: " + id));
        return calculateKpiInternal(kpi);
    }

    // ==========================================
    // INTERNAL CALCULATION ENGINE
    // ==========================================

    private KpiCalculationResultResponse calculateKpiInternal(KpiDefinition kpi) {
        String code = kpi.getKpiCode().toUpperCase().trim();
        Double calculatedValue = null;
        Long sampleCount = 0L;
        String statusReason;
        List<Double> trend = new ArrayList<>();

        switch (code) {
            case "AVG_SUPPLIER_SCORE": {
                List<Supplier> suppliers = supplierRepository.findAll().stream()
                        .filter(s -> s.getStatus() == SupplierStatus.ACTIVE && s.getOverallRating() != null && s.getTotalEvaluations() > 0)
                        .collect(Collectors.toList());
                sampleCount = (long) suppliers.size();
                if (sampleCount > 0) {
                    double sum = suppliers.stream().mapToDouble(Supplier::getOverallRating).sum();
                    calculatedValue = round(sum / sampleCount, 2);
                    trend = suppliers.stream().limit(5).map(Supplier::getOverallRating).collect(Collectors.toList());
                }
                break;
            }

            case "QUALITY_COMPLIANCE_RATE": {
                List<SupplierEvaluation> evals = evaluationRepository.findAll().stream()
                        .filter(e -> e.getStatus() != EvaluationStatus.CANCELLED)
                        .collect(Collectors.toList());
                List<EvaluationScore> qualityScores = evals.stream()
                        .flatMap(e -> e.getScores().stream())
                        .filter(s -> s.getCriteria() != null &&
                                (s.getCriteria().getName().toLowerCase().contains("qual") ||
                                 s.getCriteria().getName().toLowerCase().contains("defect") ||
                                 s.getCriteria().getName().toLowerCase().contains("complian")))
                        .collect(Collectors.toList());

                if (!qualityScores.isEmpty()) {
                    sampleCount = (long) qualityScores.size();
                    double avgPercentage = qualityScores.stream()
                            .mapToDouble(s -> (s.getScoreObtained() / (s.getMaxScore() != null ? s.getMaxScore() : 100.0)) * 100.0)
                            .average().orElse(0.0);
                    calculatedValue = round(avgPercentage, 2);
                } else if (!evals.isEmpty()) {
                    sampleCount = (long) evals.size();
                    calculatedValue = round(evals.stream().mapToDouble(SupplierEvaluation::getTotalWeightedScore).average().orElse(0.0), 2);
                }
                break;
            }

            case "ON_TIME_DELIVERY_RATE": {
                List<SupplierEvaluation> evals = evaluationRepository.findAll().stream()
                        .filter(e -> e.getStatus() != EvaluationStatus.CANCELLED)
                        .collect(Collectors.toList());
                List<EvaluationScore> deliveryScores = evals.stream()
                        .flatMap(e -> e.getScores().stream())
                        .filter(s -> s.getCriteria() != null &&
                                (s.getCriteria().getName().toLowerCase().contains("deliv") ||
                                 s.getCriteria().getName().toLowerCase().contains("time") ||
                                 s.getCriteria().getName().toLowerCase().contains("sched")))
                        .collect(Collectors.toList());

                if (!deliveryScores.isEmpty()) {
                    sampleCount = (long) deliveryScores.size();
                    double avgPercentage = deliveryScores.stream()
                            .mapToDouble(s -> (s.getScoreObtained() / (s.getMaxScore() != null ? s.getMaxScore() : 100.0)) * 100.0)
                            .average().orElse(0.0);
                    calculatedValue = round(avgPercentage, 2);
                } else if (!evals.isEmpty()) {
                    sampleCount = (long) evals.size();
                    calculatedValue = round(evals.stream().mapToDouble(SupplierEvaluation::getTotalWeightedScore).average().orElse(0.0), 2);
                }
                break;
            }

            case "CAP_COMPLETION_RATE": {
                List<SupplierImprovementAction> actions = improvementActionRepository.findAll();
                sampleCount = (long) actions.size();
                if (sampleCount > 0) {
                    long completedCount = actions.stream()
                            .filter(a -> a.getStatus() == ImprovementActionStatus.COMPLETED)
                            .count();
                    calculatedValue = round(((double) completedCount / sampleCount) * 100.0, 2);
                }
                break;
            }

            case "HIGH_RISK_SUPPLIER_RATIO": {
                List<Supplier> activeSuppliers = supplierRepository.findAll().stream()
                        .filter(s -> s.getStatus() == SupplierStatus.ACTIVE)
                        .collect(Collectors.toList());
                sampleCount = (long) activeSuppliers.size();
                if (sampleCount > 0) {
                    long highRiskCount = activeSuppliers.stream()
                            .filter(s -> s.getRatingCategory() == RatingCategory.POOR || (s.getOverallRating() != null && s.getOverallRating() < 60.0))
                            .count();
                    calculatedValue = round(((double) highRiskCount / sampleCount) * 100.0, 2);
                }
                break;
            }

            case "SLA_COMPLIANCE_RATE": {
                List<ApprovalTask> tasks = approvalTaskRepository.findAll();
                sampleCount = (long) tasks.size();
                if (sampleCount > 0) {
                    long nonEscalated = tasks.stream()
                            .filter(t -> !t.isEscalated() && t.getStatus() != TaskStatus.ESCALATED)
                            .count();
                    calculatedValue = round(((double) nonEscalated / sampleCount) * 100.0, 2);
                }
                break;
            }

            case "EVALUATION_COVERAGE_RATE": {
                List<Supplier> activeSuppliers = supplierRepository.findAll().stream()
                        .filter(s -> s.getStatus() == SupplierStatus.ACTIVE)
                        .collect(Collectors.toList());
                sampleCount = (long) activeSuppliers.size();
                if (sampleCount > 0) {
                    long evaluatedCount = activeSuppliers.stream()
                            .filter(s -> s.getTotalEvaluations() != null && s.getTotalEvaluations() > 0)
                            .count();
                    calculatedValue = round(((double) evaluatedCount / sampleCount) * 100.0, 2);
                }
                break;
            }

            case "TOP_TIER_SUPPLIER_RATIO": {
                List<Supplier> activeSuppliers = supplierRepository.findAll().stream()
                        .filter(s -> s.getStatus() == SupplierStatus.ACTIVE)
                        .collect(Collectors.toList());
                sampleCount = (long) activeSuppliers.size();
                if (sampleCount > 0) {
                    long topTierCount = activeSuppliers.stream()
                            .filter(s -> s.getRatingCategory() == RatingCategory.EXCELLENT || s.getRatingCategory() == RatingCategory.GOOD)
                            .count();
                    calculatedValue = round(((double) topTierCount / sampleCount) * 100.0, 2);
                }
                break;
            }

            default: {
                if (kpi.getCalculationType() == KpiCalculationType.AVERAGE_SCORE) {
                    List<Supplier> list = supplierRepository.findAll().stream()
                            .filter(s -> s.getOverallRating() != null)
                            .collect(Collectors.toList());
                    sampleCount = (long) list.size();
                    if (sampleCount > 0) {
                        calculatedValue = round(list.stream().mapToDouble(Supplier::getOverallRating).average().orElse(0.0), 2);
                    }
                } else if (kpi.getCalculationType() == KpiCalculationType.COUNT) {
                    calculatedValue = (double) supplierRepository.count();
                    sampleCount = 1L;
                }
                break;
            }
        }

        // Determine Status based on value and thresholds
        KpiStatus status;
        if (calculatedValue == null || sampleCount == 0) {
            status = KpiStatus.NO_DATA;
            statusReason = "Insufficient real data records to calculate KPI.";
        } else {
            boolean higherIsBetter = Boolean.TRUE.equals(kpi.getHigherIsBetter());
            Double warning = kpi.getWarningThreshold();
            Double critical = kpi.getCriticalThreshold();
            Double target = kpi.getTargetValue();

            if (higherIsBetter) {
                if (critical != null && calculatedValue < critical) {
                    status = KpiStatus.CRITICAL;
                    statusReason = String.format("Current value (%.2f) breached critical threshold (%.2f)", calculatedValue, critical);
                } else if (warning != null && calculatedValue < warning) {
                    status = KpiStatus.WARNING;
                    statusReason = String.format("Current value (%.2f) below warning threshold (%.2f)", calculatedValue, warning);
                } else {
                    status = KpiStatus.GOOD;
                    statusReason = target != null && calculatedValue >= target
                            ? String.format("Current value (%.2f) meets or exceeds target (%.2f)", calculatedValue, target)
                            : String.format("Current value (%.2f) in healthy operating range", calculatedValue);
                }
            } else {
                if (critical != null && calculatedValue > critical) {
                    status = KpiStatus.CRITICAL;
                    statusReason = String.format("Current value (%.2f) exceeded critical limit (%.2f)", calculatedValue, critical);
                } else if (warning != null && calculatedValue > warning) {
                    status = KpiStatus.WARNING;
                    statusReason = String.format("Current value (%.2f) exceeded warning threshold (%.2f)", calculatedValue, warning);
                } else {
                    status = KpiStatus.GOOD;
                    statusReason = target != null && calculatedValue <= target
                            ? String.format("Current value (%.2f) meets target threshold (%.2f)", calculatedValue, target)
                            : String.format("Current value (%.2f) in healthy operating range", calculatedValue);
                }
            }
        }

        String formattedValue = calculatedValue != null
                ? String.format("%.2f %s", calculatedValue, kpi.getUnit() != null ? kpi.getUnit() : "")
                : "N/A";

        return KpiCalculationResultResponse.builder()
                .kpiId(kpi.getId())
                .kpiCode(kpi.getKpiCode())
                .name(kpi.getName())
                .description(kpi.getDescription())
                .category(kpi.getCategory())
                .value(calculatedValue)
                .formattedValue(formattedValue.trim())
                .unit(kpi.getUnit())
                .targetValue(kpi.getTargetValue())
                .warningThreshold(kpi.getWarningThreshold())
                .criticalThreshold(kpi.getCriticalThreshold())
                .higherIsBetter(kpi.getHigherIsBetter())
                .status(status)
                .statusReason(statusReason)
                .sampleCount(sampleCount)
                .historicalTrend(trend)
                .build();
    }

    private void validateThresholds(Double warning, Double critical, Double target, Boolean higherIsBetter) {
        boolean hib = higherIsBetter == null || higherIsBetter;
        if (warning != null && critical != null) {
            if (hib && critical > warning) {
                throw new BadRequestException("For higher-is-better KPIs, critical threshold (" + critical + ") cannot be higher than warning threshold (" + warning + ").");
            }
            if (!hib && critical < warning) {
                throw new BadRequestException("For lower-is-better KPIs, critical threshold (" + critical + ") cannot be lower than warning threshold (" + warning + ").");
            }
        }
    }

    private Double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private KpiDefinitionResponse mapToResponse(KpiDefinition kpi) {
        return KpiDefinitionResponse.builder()
                .id(kpi.getId())
                .kpiCode(kpi.getKpiCode())
                .name(kpi.getName())
                .description(kpi.getDescription())
                .category(kpi.getCategory())
                .calculationType(kpi.getCalculationType())
                .unit(kpi.getUnit())
                .targetValue(kpi.getTargetValue())
                .warningThreshold(kpi.getWarningThreshold())
                .criticalThreshold(kpi.getCriticalThreshold())
                .higherIsBetter(kpi.getHigherIsBetter())
                .active(kpi.getActive())
                .createdAt(kpi.getCreatedAt())
                .updatedAt(kpi.getUpdatedAt())
                .build();
    }
}
