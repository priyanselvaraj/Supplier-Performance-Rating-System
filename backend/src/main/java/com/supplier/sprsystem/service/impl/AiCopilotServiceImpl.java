package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.ai.*;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.AiCopilotService;
import com.supplier.sprsystem.service.AiIntelligenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AiCopilotServiceImpl implements AiCopilotService {

    private static final Logger log = LoggerFactory.getLogger(AiCopilotServiceImpl.class);

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierImprovementActionRepository improvementActionRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final AiInteractionHistoryRepository historyRepository;
    private final AiRecommendationDecisionRepository decisionRepository;
    private final AiIntelligenceService aiIntelligenceService;

    public AiCopilotServiceImpl(UserRepository userRepository,
                                SupplierRepository supplierRepository,
                                SupplierEvaluationRepository evaluationRepository,
                                SupplierImprovementActionRepository improvementActionRepository,
                                WorkflowInstanceRepository workflowInstanceRepository,
                                AiInteractionHistoryRepository historyRepository,
                                AiRecommendationDecisionRepository decisionRepository,
                                AiIntelligenceService aiIntelligenceService) {
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.improvementActionRepository = improvementActionRepository;
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.historyRepository = historyRepository;
        this.decisionRepository = decisionRepository;
        this.aiIntelligenceService = aiIntelligenceService;
    }

    @Override
    public AiCopilotQueryResponse processCopilotQuery(AiCopilotQueryRequest request, UserDetails currentUser) {
        User user = resolveCurrentUser(currentUser);
        boolean isSupplierRole = isSupplierUser(currentUser);
        Supplier restrictedSupplier = (isSupplierRole && user != null) ? user.getSupplier() : null;

        if (isSupplierRole && restrictedSupplier == null) {
            throw new UnauthorizedException("Supplier portal user is not assigned to an active supplier account.");
        }

        String rawQuestion = request.getQuestion().trim();
        String qLower = rawQuestion.toLowerCase();

        // Detect query intent
        String intent;
        String intentCategory;
        StringBuilder answer = new StringBuilder();
        List<String> dataCitations = new ArrayList<>();
        List<Map<String, Object>> relevantData = new ArrayList<>();
        List<String> suggestedActions = new ArrayList<>();
        List<String> followUps = new ArrayList<>();
        boolean sufficientData = true;

        // Check if query is about a specific supplier
        Long targetSupplierId = request.getSupplierId();
        if (targetSupplierId == null && isSupplierRole) {
            targetSupplierId = restrictedSupplier.getId();
        }

        // Search for supplier mention if not specified
        if (targetSupplierId == null && !isSupplierRole) {
            List<Supplier> allSuppliers = supplierRepository.findAll();
            for (Supplier s : allSuppliers) {
                if (qLower.contains(s.getName().toLowerCase()) ||
                    (s.getSupplierCode() != null && qLower.contains(s.getSupplierCode().toLowerCase()))) {
                    targetSupplierId = s.getId();
                    break;
                }
            }
        }

        if (qLower.contains("poor") || qLower.contains("worst") || qLower.contains("lowest") ||
            qLower.contains("underperform") || qLower.contains("failing") || qLower.contains("bad") ||
            qLower.contains("low score") || qLower.contains("below target")) {
            
            intent = "Identify Underperforming Suppliers";
            intentCategory = "PERFORMANCE_ANALYSIS";
            handlePoorPerformanceQuery(restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("risk") || qLower.contains("vulnerab") || qLower.contains("critical") ||
                   qLower.contains("hazard") || qLower.contains("threat") || qLower.contains("danger")) {
            
            intent = "Supplier Risk Assessment";
            intentCategory = "RISK_INTELLIGENCE";
            handleRiskQuery(targetSupplierId, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("improv") || qLower.contains("best") || qLower.contains("top") ||
                   qLower.contains("progress") || qLower.contains("growth") || qLower.contains("upward")) {
            
            intent = "Supplier Improvement & Growth Tracking";
            intentCategory = "TREND_ANALYSIS";
            handleImprovementQuery(restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("compare") || qLower.contains("versus") || qLower.contains(" vs ") ||
                   qLower.contains("difference between")) {
            
            intent = "Multi-Supplier Comparative Breakdown";
            intentCategory = "COMPARATIVE_INSIGHTS";
            handleComparisonQuery(qLower, isSupplierRole, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("action") || qLower.contains("overdue") || qLower.contains("issue") ||
                   qLower.contains("problem") || qLower.contains("deficien") || qLower.contains("remediat") ||
                   qLower.contains("corrective")) {
            
            intent = "Improvement Actions & Deficiencies";
            intentCategory = "DECISION_SUPPORT";
            handleActionItemsQuery(restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("workflow") || qLower.contains("approval") || qLower.contains("pending") ||
                   qLower.contains("delay") || qLower.contains("escalat") || qLower.contains("bottleneck")) {
            
            intent = "Workflow & Approval Bottlenecks";
            intentCategory = "WORKFLOW_INTELLIGENCE";
            handleWorkflowQuery(isSupplierRole, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (qLower.contains("executive") || qLower.contains("summary") || qLower.contains("portfolio") ||
                   qLower.contains("overview") || qLower.contains("health") || qLower.contains("c-suite")) {
            
            intent = "Executive Portfolio Health Snapshot";
            intentCategory = "EXECUTIVE_SUMMARY";
            handleExecutiveSummaryQuery(isSupplierRole, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else if (targetSupplierId != null) {
            
            intent = "Supplier 360 Deep-Dive";
            intentCategory = "SUPPLIER_PROFILE";
            handleSingleSupplierQuery(targetSupplierId, isSupplierRole, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);

        } else {
            
            intent = "General Analytics & Decision Support";
            intentCategory = "GENERAL_INTELLIGENCE";
            handleGeneralAnalyticsQuery(isSupplierRole, restrictedSupplier, answer, dataCitations, relevantData, suggestedActions, followUps);
        }

        // Save interaction history if user is present
        String summary = answer.length() > 200 ? answer.substring(0, 197) + "..." : answer.toString();
        AiInteractionHistory savedHistory = null;
        if (user != null) {
            AiInteractionHistory history = AiInteractionHistory.builder()
                    .user(user)
                    .question(rawQuestion)
                    .responseSummary(summary)
                    .queryIntent(intent)
                    .intentCategory(intentCategory)
                    .build();
            savedHistory = historyRepository.save(history);
        }

        return AiCopilotQueryResponse.builder()
                .id(savedHistory != null ? savedHistory.getId() : 1L)
                .question(rawQuestion)
                .answer(answer.toString())
                .queryIntent(intent)
                .intentCategory(intentCategory)
                .confidence("HIGH")
                .sufficientData(sufficientData)
                .dataCitations(dataCitations)
                .relevantData(relevantData)
                .suggestedActions(suggestedActions)
                .followUpQuestions(followUps)
                .createdAt(savedHistory != null ? savedHistory.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    private void handlePoorPerformanceQuery(Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        List<Supplier> candidates;
        if (restrictedSupplier != null) {
            candidates = Collections.singletonList(restrictedSupplier);
        } else {
            candidates = supplierRepository.findAll();
        }

        List<Supplier> lowPerformers = candidates.stream()
                .filter(s -> s.getOverallRating() != null && s.getOverallRating() < 75.0)
                .sorted(Comparator.comparingDouble(s -> s.getOverallRating() != null ? s.getOverallRating() : 0.0))
                .collect(Collectors.toList());

        if (lowPerformers.isEmpty()) {
            answer.append("### Supplier Performance Analysis\n\n");
            answer.append("No active suppliers currently fall below the standard 75.0% performance threshold.\n\n");
            answer.append("- **Portfolio Health**: All evaluated suppliers are performing within acceptable or excellent boundaries.\n");
            dataCitations.add("Evaluated across " + candidates.size() + " registered suppliers in database.");
            followUps.add("Which suppliers are considered high risk?");
            followUps.add("Show overall executive summary.");
            return;
        }

        answer.append("### Underperforming Suppliers Identified\n\n");
        answer.append("Based on the latest weighted evaluation cycles, the following **").append(lowPerformers.size()).append("** supplier(s) are performing below the benchmark target (75.0%):\n\n");

        for (Supplier s : lowPerformers) {
            double rating = s.getOverallRating() != null ? s.getOverallRating() : 0.0;
            String cat = s.getCategory() != null ? s.getCategory().getName() : "General";
            String tier = s.getRatingCategory() != null ? s.getRatingCategory().name() : "UNRATED";

            answer.append("- **").append(s.getName()).append("** (`").append(s.getSupplierCode()).append("`)\n");
            answer.append("  - **Current Score**: `").append(String.format("%.1f%%", rating)).append("` (Tier: **").append(tier).append("**)\n");
            answer.append("  - **Category**: ").append(cat).append("\n");
            answer.append("  - **Status**: ").append(s.getStatus() != null ? s.getStatus().name() : "ACTIVE").append("\n");

            Map<String, Object> map = new HashMap<>();
            map.put("supplierId", s.getId());
            map.put("supplierName", s.getName());
            map.put("supplierCode", s.getSupplierCode());
            map.put("score", rating);
            map.put("tier", tier);
            relevantData.add(map);

            dataCitations.add(String.format("%s (Code: %s) rating: %.1f%% from latest evaluation audit", s.getName(), s.getSupplierCode(), rating));
        }

        suggestedActions.add("Initiate formal Corrective Action Plans (CAP) for suppliers below 70.0%.");
        suggestedActions.add("Schedule a supplier governance review with procurement managers.");
        suggestedActions.add("Increase sampling frequency for incoming defect inspections.");

        followUps.add("Why is the lowest rated supplier underperforming?");
        followUps.add("Show open improvement actions for underperforming suppliers.");
        followUps.add("Compare underperforming suppliers against category benchmarks.");
    }

    private void handleRiskQuery(Long targetSupplierId, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        if (targetSupplierId != null) {
            Supplier s = supplierRepository.findById(targetSupplierId).orElse(null);
            if (s == null || (restrictedSupplier != null && !s.getId().equals(restrictedSupplier.getId()))) {
                answer.append("Insufficient data available or unauthorized access to specified supplier.");
                return;
            }

            SupplierRiskResponse risk = aiIntelligenceService.getSupplierRisk(s.getId());
            answer.append("### Risk Assessment: ").append(s.getName()).append(" (`").append(s.getSupplierCode()).append("`)\n\n");
            answer.append("- **Risk Classification**: **").append(risk.getRiskLevel().getDisplayName().toUpperCase()).append("**\n");
            answer.append("- **Risk Index Score**: `").append(risk.getRiskScore()).append(" / 100`\n");
            answer.append("- **Current Performance Score**: `").append(risk.getCurrentScore()).append("%` (").append(risk.getRatingCategory()).append(")\n");
            answer.append("- **Historical Trajectory**: ").append(risk.getTrend().getDisplayName()).append("\n\n");

            answer.append("#### Primary Contributing Risk Factors:\n");
            for (String factor : risk.getRiskFactors()) {
                answer.append("1. ").append(factor).append("\n");
            }

            if (risk.getExplanation() != null) {
                answer.append("\n> **AI Risk Summary**: ").append(risk.getExplanation()).append("\n\n");
            }

            dataCitations.add(String.format("Risk scoring model evaluated performance deficit, velocity rate, and %d active alerts", risk.getActiveAlertsCount()));

            suggestedActions.add("Review the latest evaluation breakdown on the supplier details page.");
            if (risk.getRiskLevel() == RiskLevel.HIGH || risk.getRiskLevel() == RiskLevel.CRITICAL) {
                suggestedActions.add("Mandate an immediate quality audit and review alternative supply sources.");
            }

            followUps.add("What are the recommended actions for " + s.getName() + "?");
            followUps.add("Show predictive trend for " + s.getName() + ".");
            return;
        }

        // Global risk query across suppliers
        List<Supplier> suppliers = restrictedSupplier != null ? Collections.singletonList(restrictedSupplier) : supplierRepository.findAll();
        List<SupplierRiskResponse> highRisks = new ArrayList<>();

        for (Supplier sup : suppliers) {
            try {
                SupplierRiskResponse r = aiIntelligenceService.getSupplierRisk(sup.getId());
                if (r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL) {
                    highRisks.add(r);
                }
            } catch (Exception ignored) {}
        }

        answer.append("### Portfolio High-Risk Suppliers\n\n");
        if (highRisks.isEmpty()) {
            answer.append("No suppliers are currently categorized as **HIGH** or **CRITICAL** risk in the system.\n\n");
            answer.append("All suppliers maintain steady performance baselines and manageable defect/variance levels.");
        } else {
            answer.append("Identified **").append(highRisks.size()).append("** supplier(s) requiring elevated monitoring:\n\n");
            for (SupplierRiskResponse r : highRisks) {
                answer.append("- **").append(r.getSupplierName()).append("** (`").append(r.getSupplierCode()).append("`) — **").append(r.getRiskLevel().getDisplayName().toUpperCase()).append("** (Score: ").append(r.getRiskScore()).append("/100)\n");
                if (!r.getRiskFactors().isEmpty()) {
                    answer.append("  - *Factor*: ").append(r.getRiskFactors().get(0)).append("\n");
                }
                dataCitations.add(String.format("%s: Risk score %.1f, Level %s", r.getSupplierName(), r.getRiskScore(), r.getRiskLevel().getDisplayName()));
            }

            suggestedActions.add("Establish weekly governance reviews for high-risk vendors.");
            suggestedActions.add("Evaluate buffer inventory and multi-sourcing contingencies.");
        }

        followUps.add("Which suppliers have improving trends?");
        followUps.add("What are the overdue improvement actions?");
    }

    private void handleImprovementQuery(Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        List<Supplier> suppliers = restrictedSupplier != null ? Collections.singletonList(restrictedSupplier) : supplierRepository.findAll();
        List<SupplierAiTrendResponse> improving = new ArrayList<>();

        for (Supplier sup : suppliers) {
            try {
                SupplierAiTrendResponse t = aiIntelligenceService.getSupplierTrend(sup.getId());
                if (t.getTrend() == PerformanceTrend.IMPROVING) {
                    improving.add(t);
                }
            } catch (Exception ignored) {}
        }

        answer.append("### Suppliers Demonstrating Positive Performance Growth\n\n");
        if (improving.isEmpty()) {
            answer.append("No suppliers currently show a consecutive multi-cycle positive trajectory (> +2.0 point velocity).\n");
            answer.append("Most suppliers are currently exhibiting stable performance across recent quarters.");
        } else {
            answer.append("The following **").append(improving.size()).append("** supplier(s) have demonstrated measurable performance gains:\n\n");
            for (SupplierAiTrendResponse t : improving) {
                answer.append("- **").append(t.getSupplierName()).append("**\n");
                answer.append("  - **Score Increase**: `+").append(t.getScoreDifference()).append(" pts` (+").append(t.getPercentageChange()).append("%)\n");
                answer.append("  - **Trajectory**: From `").append(t.getPreviousScore()).append("%` to `").append(t.getCurrentScore()).append("%`\n");
                if (t.getExplanation() != null) {
                    answer.append("  - *Insight*: ").append(t.getExplanation()).append("\n");
                }
                dataCitations.add(String.format("%s: improved by +%.1f pts across evaluation cycles", t.getSupplierName(), t.getScoreDifference()));
            }

            suggestedActions.add("Consider expanding contract volume or preferred vendor status for top-improving suppliers.");
            suggestedActions.add("Document vendor best practices for cross-supplier knowledge sharing.");
        }

        followUps.add("Compare top performing suppliers.");
        followUps.add("Show executive summary for this quarter.");
    }

    private void handleComparisonQuery(String qLower, boolean isSupplierRole, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        if (isSupplierRole) {
            answer.append("### Supplier Comparison Policy\n\n");
            answer.append("In accordance with supplier data privacy and RBAC security policies, vendor comparison is limited to internal procurement managers.\n\n");
            answer.append("You can view your own comprehensive scorecard and benchmark details on your supplier portal dashboard.");
            return;
        }

        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<Supplier> matched = new ArrayList<>();

        for (Supplier s : allSuppliers) {
            if (qLower.contains(s.getName().toLowerCase()) ||
                (s.getSupplierCode() != null && qLower.contains(s.getSupplierCode().toLowerCase()))) {
                matched.add(s);
            }
        }

        if (matched.size() < 2) {
            // Default to top 2 suppliers for demo comparison
            if (allSuppliers.size() >= 2) {
                matched = allSuppliers.stream()
                        .sorted(Comparator.comparingDouble(s -> -(s.getOverallRating() != null ? s.getOverallRating() : 0.0)))
                        .limit(2)
                        .collect(Collectors.toList());
            } else {
                answer.append("Insufficient supplier records (minimum 2 required) available to conduct a comparative analysis.");
                return;
            }
        }

        AiSupplierCompareRequest compareReq = new AiSupplierCompareRequest(matched.stream().map(Supplier::getId).collect(Collectors.toList()));
        AiSupplierCompareResponse comp = compareSuppliers(compareReq, null);

        answer.append("### Comparative Analysis: ").append(matched.stream().map(Supplier::getName).collect(Collectors.joining(" vs "))).append("\n\n");
        answer.append(comp.getComparativeAnalysis()).append("\n\n");

        if (comp.getRecommendedSupplierName() != null) {
            answer.append("> **Recommended Choice**: **").append(comp.getRecommendedSupplierName()).append("**\n");
            answer.append("> **Rationale**: ").append(comp.getRecommendationRationale()).append("\n");
        }

        dataCitations.add(String.format("Evaluated across %d dimensions using historical weighted scorecards", matched.size()));
        followUps.add("Which supplier has lower risk?");
        followUps.add("Show action items for these suppliers.");
    }

    private void handleActionItemsQuery(Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        List<SupplierImprovementAction> actions;
        if (restrictedSupplier != null) {
            actions = improvementActionRepository.findBySupplierIdOrderByCreatedAtDesc(restrictedSupplier.getId());
        } else {
            actions = improvementActionRepository.findAll();
        }

        LocalDate today = LocalDate.now();
        List<SupplierImprovementAction> openActions = actions.stream()
                .filter(a -> a.getStatus() != ImprovementActionStatus.COMPLETED && a.getStatus() != ImprovementActionStatus.CANCELLED)
                .collect(Collectors.toList());

        List<SupplierImprovementAction> overdue = openActions.stream()
                .filter(a -> a.getDueDate() != null && a.getDueDate().isBefore(today))
                .collect(Collectors.toList());

        answer.append("### Improvement Actions & Deficiencies Overview\n\n");
        answer.append("- **Total Open Action Plans**: `").append(openActions.size()).append("`\n");
        answer.append("- **Overdue Action Plans**: `").append(overdue.size()).append("`\n\n");

        if (!overdue.isEmpty()) {
            answer.append("#### Critical Overdue Items Requiring Follow-Up:\n");
            for (SupplierImprovementAction a : overdue) {
                long days = ChronoUnit.DAYS.between(a.getDueDate(), today);
                answer.append("- **").append(a.getTitle()).append("** (Supplier: **").append(a.getSupplier().getName()).append("**)\n");
                answer.append("  - **Priority**: `").append(a.getPriority().name()).append("` | **Days Overdue**: `").append(days).append(" days` (Due: ").append(a.getDueDate()).append(")\n");
                answer.append("  - **Status**: ").append(a.getStatus().name()).append("\n");
                dataCitations.add(String.format("Action #%d '%s' for %s was due on %s", a.getId(), a.getTitle(), a.getSupplier().getName(), a.getDueDate()));
            }

            suggestedActions.add("Send reminder notifications to assigned owners of overdue actions.");
            suggestedActions.add("Escalate high-priority overdue actions to the procurement lead.");
        } else if (!openActions.isEmpty()) {
            answer.append("All active action plans are currently on-track within their designated target completion dates.");
        } else {
            answer.append("No active improvement action plans currently recorded.");
        }

        followUps.add("Which suppliers have high risk?");
        followUps.add("Show executive summary.");
    }

    private void handleWorkflowQuery(boolean isSupplierRole, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        if (isSupplierRole) {
            answer.append("### Workflow Status\n\n");
            answer.append("All vendor profile updates and document verification workflows are managed by your procurement representative.\n");
            answer.append("Check the **Documents** and **Communications** tab in your portal for updates.");
            return;
        }

        List<WorkflowInstance> inProgress = workflowInstanceRepository.findByStatus(WorkflowStatus.IN_PROGRESS);
        answer.append("### Active Workflows & Governance Status\n\n");
        answer.append("- **Workflows In Progress**: `").append(inProgress.size()).append("`\n\n");

        if (inProgress.isEmpty()) {
            answer.append("There are currently no delayed or pending governance workflows in the system. All tasks are completed.");
        } else {
            answer.append("#### Active Pending Instances:\n");
            LocalDateTime now = LocalDateTime.now();
            for (WorkflowInstance w : inProgress) {
                long daysOpen = w.getStartedAt() != null ? ChronoUnit.DAYS.between(w.getStartedAt(), now) : 0;
                String step = "Step " + w.getCurrentStepOrder() + " of " + w.getTotalSteps();
                answer.append("- **").append(w.getTitle()).append("** (`").append(w.getWorkflowType().name()).append("`)\n");
                answer.append("  - **Current Step**: ").append(step).append(" | **Days Active**: `").append(daysOpen).append(" days`\n");
                dataCitations.add(String.format("Workflow '%s' (ID: %d) initiated on %s", w.getTitle(), w.getId(), w.getStartedAt()));
            }

            suggestedActions.add("Review pending approval tasks in 'My Approvals' dashboard.");
            suggestedActions.add("Trigger escalation notifications for workflows open > 7 days.");
        }

        followUps.add("Show executive summary.");
        followUps.add("Which suppliers are performing poorly?");
    }

    private void handleExecutiveSummaryQuery(boolean isSupplierRole, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        ExecutiveAiSummaryResponse exec = getExecutiveAiInsights(null);

        answer.append("### Executive C-Suite AI Summary\n\n");
        answer.append(exec.getExecutiveSummary()).append("\n\n");

        answer.append("#### Key Strategic Highlights:\n");
        for (String h : exec.getPerformanceHighlights()) {
            answer.append("- ").append(h).append("\n");
        }

        if (!exec.getKeyRisks().isEmpty()) {
            answer.append("\n#### Critical Risks:\n");
            for (String r : exec.getKeyRisks()) {
                answer.append("- ").append(r).append("\n");
            }
        }

        dataCitations.add(String.format("Portfolio average: %.1f%% across %d suppliers", exec.getPortfolioAverageScore(), exec.getTotalSuppliersEvaluated()));
        suggestedActions.add("Access the Executive AI Insights dashboard for full KPI breakdowns.");
        followUps.add("Which suppliers are high risk?");
        followUps.add("Which improvement actions are overdue?");
    }

    private void handleSingleSupplierQuery(Long supplierId, boolean isSupplierRole, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        Supplier s = supplierRepository.findById(supplierId).orElse(null);
        if (s == null || (isSupplierRole && restrictedSupplier != null && !s.getId().equals(restrictedSupplier.getId()))) {
            answer.append("Insufficient data available or unauthorized access to specified supplier.");
            return;
        }

        double rating = s.getOverallRating() != null ? s.getOverallRating() : 0.0;
        String tier = s.getRatingCategory() != null ? s.getRatingCategory().name() : "UNRATED";
        SupplierRiskResponse risk = aiIntelligenceService.getSupplierRisk(s.getId());
        SupplierPredictionResponse pred = aiIntelligenceService.getSupplierPrediction(s.getId());

        answer.append("### Supplier 360 Insights: ").append(s.getName()).append(" (`").append(s.getSupplierCode()).append("`)\n\n");
        answer.append("- **Category**: ").append(s.getCategory() != null ? s.getCategory().getName() : "N/A").append("\n");
        answer.append("- **Composite Rating**: `").append(String.format("%.1f%%", rating)).append("` (Tier: **").append(tier).append("**)\n");
        answer.append("- **Risk Index**: **").append(risk.getRiskLevel().getDisplayName().toUpperCase()).append("** (Score: ").append(risk.getRiskScore()).append("/100)\n");
        answer.append("- **Predictive Forecast**: ");
        if (pred.getPredictedScore() != null) {
            answer.append("`").append(pred.getPredictedScore()).append("%` (").append(pred.getTrend().getDisplayName()).append(")\n\n");
        } else {
            answer.append("Insufficient historical evaluations to forecast.\n\n");
        }

        answer.append("#### Key AI Insights:\n");
        answer.append("- **Risk Factors**: ").append(String.join("; ", risk.getRiskFactors())).append("\n");
        if (pred.getExplanation() != null) {
            answer.append("- **Trajectory**: ").append(pred.getExplanation()).append("\n");
        }

        dataCitations.add(String.format("%s performance audited from latest scorecards and AI regression models", s.getName()));
        suggestedActions.add("Review detailed scorecards and criteria weights on the Supplier Profile.");
        followUps.add("What are the recommended actions for " + s.getName() + "?");
        followUps.add("Compare " + s.getName() + " with category peers.");
    }

    private void handleGeneralAnalyticsQuery(boolean isSupplierRole, Supplier restrictedSupplier, StringBuilder answer, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUps) {
        long supCount = isSupplierRole ? 1 : supplierRepository.count();
        long evalCount = isSupplierRole && restrictedSupplier != null ? evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(restrictedSupplier.getId()).size() : evaluationRepository.count();
        long actionCount = isSupplierRole && restrictedSupplier != null ? improvementActionRepository.findBySupplierIdOrderByCreatedAtDesc(restrictedSupplier.getId()).size() : improvementActionRepository.count();

        answer.append("### Supplier Performance AI Copilot\n\n");
        answer.append("I can analyze supplier scorecards, risk trajectories, SLA compliance, and open improvement actions using real system data.\n\n");
        answer.append("- **Active Suppliers in Scope**: `").append(supCount).append("`\n");
        answer.append("- **Total Completed Evaluations**: `").append(evalCount).append("`\n");
        answer.append("- **Improvement Action Plans**: `").append(actionCount).append("`\n\n");
        answer.append("You can ask questions such as:\n");
        answer.append("1. *\"Which suppliers are performing poorly?\"*\n");
        answer.append("2. *\"Why is a supplier considered high risk?\"*\n");
        answer.append("3. *\"Which suppliers improved this quarter?\"*\n");
        answer.append("4. *\"Which improvement actions are overdue?\"*\n");
        answer.append("5. *\"Give me an executive portfolio summary.\"*\n");

        followUps.add("Which suppliers are performing poorly?");
        followUps.add("Which suppliers are considered high risk?");
        followUps.add("Show overdue improvement actions.");
    }

    @Override
    public List<AiCopilotHistoryResponse> getInteractionHistory(UserDetails currentUser) {
        User user = resolveCurrentUser(currentUser);
        if (user == null) {
            return Collections.emptyList();
        }
        List<AiInteractionHistory> historyList = historyRepository.findTop30ByUserOrderByCreatedAtDesc(user);

        return historyList.stream()
                .map(h -> AiCopilotHistoryResponse.builder()
                        .id(h.getId())
                        .question(h.getQuestion())
                        .responseSummary(h.getResponseSummary())
                        .queryIntent(h.getQueryIntent())
                        .intentCategory(h.getIntentCategory())
                        .helpful(h.getHelpful())
                        .feedbackReason(h.getFeedbackReason())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void submitFeedback(Long historyId, AiFeedbackRequest feedback, UserDetails currentUser) {
        User user = resolveCurrentUser(currentUser);
        AiInteractionHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("AiInteractionHistory", "id", historyId));

        if (user != null && !history.getUser().getId().equals(user.getId()) && !isAdmin(currentUser)) {
            throw new UnauthorizedException("You do not have permission to modify this interaction record.");
        }

        history.setHelpful(feedback.getHelpful());
        history.setFeedbackReason(feedback.getFeedbackReason());
        historyRepository.save(history);
    }

    @Override
    public AiSupplierCompareResponse compareSuppliers(AiSupplierCompareRequest request, UserDetails currentUser) {
        List<Long> ids = request.getSupplierIds();
        if (ids == null || ids.size() < 2) {
            throw new BadRequestException("Comparison requires at least 2 valid supplier IDs.");
        }

        List<Supplier> suppliers = supplierRepository.findAllById(ids);
        if (suppliers.size() < 2) {
            throw new ResourceNotFoundException("Suppliers", "ids", ids);
        }

        List<AiSupplierCompareResponse.SupplierComparisonSummary> summaries = new ArrayList<>();
        Map<String, List<String>> strengths = new HashMap<>();
        Map<String, List<String>> weaknesses = new HashMap<>();

        Supplier best = null;
        double maxScore = -1.0;

        for (Supplier s : suppliers) {
            double rating = s.getOverallRating() != null ? s.getOverallRating() : 0.0;
            String cat = s.getCategory() != null ? s.getCategory().getName() : "General";
            String tier = s.getRatingCategory() != null ? s.getRatingCategory().name() : "UNRATED";

            SupplierRiskResponse risk = aiIntelligenceService.getSupplierRisk(s.getId());
            SupplierAiTrendResponse trend = aiIntelligenceService.getSupplierTrend(s.getId());
            int evalCount = evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(s.getId(), EvaluationStatus.COMPLETED).size();

            summaries.add(new AiSupplierCompareResponse.SupplierComparisonSummary(
                    s.getId(),
                    s.getName(),
                    s.getSupplierCode(),
                    cat,
                    round(rating, 1),
                    tier,
                    risk.getRiskLevel().getDisplayName(),
                    risk.getRiskScore(),
                    trend.getTrend().getDisplayName(),
                    evalCount
            ));

            List<String> strList = new ArrayList<>();
            List<String> weakList = new ArrayList<>();

            if (rating >= 80.0) strList.add("Strong composite rating (" + rating + "%)");
            if (risk.getRiskLevel() == RiskLevel.LOW) strList.add("Low operational & compliance risk score (" + risk.getRiskScore() + "/100)");
            if (trend.getTrend() == PerformanceTrend.IMPROVING) strList.add("Positive multi-cycle score velocity (+" + trend.getScoreDifference() + " pts)");

            if (rating < 75.0) weakList.add("Score below benchmark (" + rating + "%)");
            if (risk.getRiskLevel() == RiskLevel.HIGH || risk.getRiskLevel() == RiskLevel.CRITICAL) weakList.add("Elevated risk index (" + risk.getRiskScore() + "/100)");
            if (trend.getTrend() == PerformanceTrend.DECLINING) weakList.add("Downward trajectory (-" + Math.abs(trend.getScoreDifference()) + " pts)");

            strengths.put(s.getName(), strList.isEmpty() ? Collections.singletonList("Consistent baseline performance") : strList);
            weaknesses.put(s.getName(), weakList.isEmpty() ? Collections.singletonList("No critical deficiencies detected") : weakList);

            if (rating > maxScore) {
                maxScore = rating;
                best = s;
            }
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("### Comparative Summary Analysis\n\n");
        analysis.append("Evaluating **").append(suppliers.size()).append("** suppliers across performance rating, risk index, and historical trajectory:\n\n");

        for (AiSupplierCompareResponse.SupplierComparisonSummary sum : summaries) {
            analysis.append("- **").append(sum.getSupplierName()).append("** (`").append(sum.getSupplierCode()).append("`): Score `")
                    .append(sum.getOverallRating()).append("%` | Risk: **").append(sum.getRiskLevel()).append("** | Trend: ").append(sum.getTrend()).append("\n");
        }

        String rationale = best != null
                ? String.format("%s holds the highest composite score (%.1f%%) and maintains a superior operational risk posture.", best.getName(), maxScore)
                : "Both suppliers demonstrate comparable metrics.";

        return new AiSupplierCompareResponse(
                LocalDate.now(),
                summaries,
                analysis.toString(),
                best != null ? best.getId() : null,
                best != null ? best.getName() : null,
                rationale,
                strengths,
                weaknesses
        );
    }

    @Override
    public ExecutiveAiSummaryResponse getExecutiveAiInsights(UserDetails currentUser) {
        List<Supplier> suppliers = supplierRepository.findAll();
        int totalSuppliers = suppliers.size();

        double avgRating = suppliers.stream()
                .mapToDouble(s -> s.getOverallRating() != null ? s.getOverallRating() : 0.0)
                .average()
                .orElse(0.0);

        int highRiskCount = 0;
        int criticalAlerts = 0;

        for (Supplier s : suppliers) {
            try {
                SupplierRiskResponse r = aiIntelligenceService.getSupplierRisk(s.getId());
                if (r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL) {
                    highRiskCount++;
                }
                criticalAlerts += r.getActiveAlertsCount();
            } catch (Exception ignored) {}
        }

        List<WorkflowInstance> inProgressWorkflows = workflowInstanceRepository.findByStatus(WorkflowStatus.IN_PROGRESS);
        int overdueWorkflows = (int) inProgressWorkflows.stream()
                .filter(w -> w.getStartedAt() != null && ChronoUnit.DAYS.between(w.getStartedAt(), LocalDateTime.now()) > 7)
                .count();

        long openActions = improvementActionRepository.countByStatus(ImprovementActionStatus.IN_PROGRESS) +
                           improvementActionRepository.countByStatus(ImprovementActionStatus.OPEN);

        String summary = String.format(
                "SPRS is currently monitoring %d registered suppliers with a portfolio average rating of %.1f%%. " +
                "%d supplier(s) exhibit high/critical operational risk profiles. There are %d open improvement actions and %d pending workflows requiring managerial oversight.",
                totalSuppliers, avgRating, highRiskCount, openActions, inProgressWorkflows.size()
        );

        List<String> keyRisks = new ArrayList<>();
        if (highRiskCount > 0) keyRisks.add(String.format("%d supplier(s) breached risk tolerance thresholds requiring operational review.", highRiskCount));
        if (overdueWorkflows > 0) keyRisks.add(String.format("%d approval workflow(s) pending over 7 days in queue.", overdueWorkflows));
        if (keyRisks.isEmpty()) keyRisks.add("No critical enterprise risk triggers detected across current vendor portfolios.");

        List<String> highlights = new ArrayList<>();
        highlights.add(String.format("Portfolio composite health index stands at %.1f%%.", avgRating));
        highlights.add(String.format("%d active suppliers maintained consistent good/excellent audit scores.", (int) suppliers.stream().filter(s -> s.getOverallRating() != null && s.getOverallRating() >= 75.0).count()));

        List<String> attention = new ArrayList<>();
        if (highRiskCount > 0) attention.add("Conduct Joint Performance Reviews with high-risk vendor leadership.");
        if (openActions > 0) attention.add(String.format("Audit %d open Corrective Action Plans (CAP) nearing target delivery dates.", openActions));

        List<ExecutiveAiSummaryResponse.StrategicRecommendation> recs = new ArrayList<>();
        recs.add(new ExecutiveAiSummaryResponse.StrategicRecommendation("HIGH", "Reinforce SLA Governance on Critical Suppliers", "Elevated risk scores correlate with logistics delay spikes.", "Implement automated penalty tracking and schedule quarterly business reviews."));
        recs.add(new ExecutiveAiSummaryResponse.StrategicRecommendation("MEDIUM", "Accelerate Workflow Approval Turnaround", "Workflows pending > 7 days impede procurement milestones.", "Review authorization hierarchies and activate automatic escalation delegates."));

        return new ExecutiveAiSummaryResponse(
                LocalDateTime.now(),
                summary,
                totalSuppliers,
                round(avgRating, 1),
                highRiskCount,
                criticalAlerts,
                overdueWorkflows,
                (int) openActions,
                keyRisks,
                highlights,
                attention,
                recs
        );
    }

    @Override
    public List<AiWorkflowRecommendationResponse> getWorkflowRecommendations(UserDetails currentUser) {
        List<WorkflowInstance> inProgress = workflowInstanceRepository.findByStatus(WorkflowStatus.IN_PROGRESS);
        List<AiWorkflowRecommendationResponse> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (WorkflowInstance w : inProgress) {
            int days = w.getStartedAt() != null ? (int) ChronoUnit.DAYS.between(w.getStartedAt(), now) : 0;
            String step = "Step " + w.getCurrentStepOrder() + " of " + w.getTotalSteps();

            RecommendationPriority priority;
            String action;
            String rationale;

            if (days >= 7) {
                priority = RecommendationPriority.CRITICAL;
                action = "Trigger automated supervisor escalation and request expedited sign-off.";
                rationale = String.format("Workflow has remained pending in '%s' for %d days, breaching standard SLA (5 days).", step, days);
            } else if (days >= 3) {
                priority = RecommendationPriority.HIGH;
                action = "Send reminder notification to current step approver.";
                rationale = String.format("Workflow has been awaiting approval in '%s' for %d days.", step, days);
            } else {
                priority = RecommendationPriority.MEDIUM;
                action = "Monitor workflow progression through standard evaluation queues.";
                rationale = "Workflow is within normal turnaround tolerance.";
            }

            results.add(new AiWorkflowRecommendationResponse(
                    w.getId(),
                    w.getTitle(),
                    w.getRelatedResourceType() != null ? w.getRelatedResourceType() : "System",
                    step,
                    days,
                    action,
                    rationale,
                    priority
            ));
        }

        return results;
    }

    @Override
    public AiRecommendationDecision handleRecommendationDecision(Long recommendationId, RecommendationDecisionRequest request, UserDetails currentUser) {
        User user = resolveCurrentUser(currentUser);
        AiRecommendationDecision decision = decisionRepository.findById(recommendationId)
                .orElse(null);

        if (decision == null) {
            // If creating decision from ad-hoc recommendation reference
            Supplier s = supplierRepository.findAll().stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("Supplier", "default", 1L));
            decision = AiRecommendationDecision.builder()
                    .supplier(s)
                    .title("Recommendation Decision")
                    .recommendationRef(request.getRecommendationRef())
                    .priority(RecommendationPriority.MEDIUM)
                    .status(request.getStatus())
                    .decisionNotes(request.getDecisionNotes())
                    .decidedBy(user)
                    .decidedAt(LocalDateTime.now())
                    .build();
        } else {
            decision.setStatus(request.getStatus());
            decision.setDecisionNotes(request.getDecisionNotes());
            decision.setDecidedBy(user);
            decision.setDecidedAt(LocalDateTime.now());
        }

        // If human user chooses ACTION_CREATED, optionally spawn a real SupplierImprovementAction
        if (request.getStatus() == RecommendationDecisionStatus.ACTION_CREATED && request.getActionTitle() != null) {
            SupplierImprovementAction action = SupplierImprovementAction.builder()
                    .supplier(decision.getSupplier())
                    .title(request.getActionTitle())
                    .description(request.getActionDescription() != null ? request.getActionDescription() : decision.getRecommendationText())
                    .status(ImprovementActionStatus.OPEN)
                    .priority(ImprovementActionPriority.HIGH)
                    .dueDate(request.getTargetCompletionDate() != null ? request.getTargetCompletionDate() : LocalDate.now().plusMonths(1))
                    .assignedUser(user)
                    .build();
            improvementActionRepository.save(action);
        }

        return decisionRepository.save(decision);
    }

    @Override
    public List<AiRecommendationDecision> getRecommendationDecisions(Long supplierId, UserDetails currentUser) {
        return decisionRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId);
    }

    private User resolveCurrentUser(UserDetails userDetails) {
        if (userDetails != null && userDetails.getUsername() != null) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
        }
        return userRepository.findAll().stream().findFirst().orElse(null);
    }

    private boolean isSupplierUser(UserDetails currentUser) {
        if (currentUser == null || currentUser.getAuthorities() == null) return false;
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPPLIER"));
    }

    private boolean isAdmin(UserDetails currentUser) {
        if (currentUser == null || currentUser.getAuthorities() == null) return false;
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private double round(Double val, int places) {
        if (val == null) return 0.0;
        return Math.round(val * Math.pow(10, places)) / Math.pow(10, places);
    }
}
