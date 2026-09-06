package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;
import com.supplier.sprsystem.model.entity.RiskLevel;
import com.supplier.sprsystem.model.entity.SupplierImprovementAction;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import com.supplier.sprsystem.repository.NotificationRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierImprovementActionRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.AiIntelligenceService;
import com.supplier.sprsystem.service.MonitoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MonitoringServiceImpl implements MonitoringService {

    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierImprovementActionRepository actionRepository;
    private final NotificationRepository notificationRepository;
    private final AiIntelligenceService aiIntelligenceService;

    public MonitoringServiceImpl(SupplierRepository supplierRepository,
                                 SupplierEvaluationRepository evaluationRepository,
                                 SupplierImprovementActionRepository actionRepository,
                                 NotificationRepository notificationRepository,
                                 AiIntelligenceService aiIntelligenceService) {
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.actionRepository = actionRepository;
        this.notificationRepository = notificationRepository;
        this.aiIntelligenceService = aiIntelligenceService;
    }

    @Override
    public MonitoringSummaryResponse getMonitoringSummary(Long currentUserId) {
        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByStatus(SupplierStatus.ACTIVE);

        long openActions = actionRepository.countByStatus(ImprovementActionStatus.OPEN);
        long inProgressActions = actionRepository.countByStatus(ImprovementActionStatus.IN_PROGRESS);
        long completedActions = actionRepository.countByStatus(ImprovementActionStatus.COMPLETED);

        long unreadNotifications = currentUserId != null ? notificationRepository.countByUserIdAndIsReadFalse(currentUserId) : 0L;

        AiDashboardResponse aiDashboard = aiIntelligenceService.getAiDashboardSummary();
        int highRiskCount = aiDashboard.getHighRiskCount() + aiDashboard.getCriticalRiskCount();
        int criticalAlertsCount = aiDashboard.getCriticalAlerts().size();

        // Retrieve urgent actions (OPEN or IN_PROGRESS)
        List<SupplierImprovementAction> urgentActionsList = actionRepository.findTop5ByStatusInOrderByCreatedAtDesc(
                List.of(ImprovementActionStatus.OPEN, ImprovementActionStatus.IN_PROGRESS));

        List<ImprovementActionResponse> urgentActions = urgentActionsList.stream()
                .map(this::mapActionToResponse)
                .collect(Collectors.toList());

        // Determine real-time health indicator
        String healthStatus;
        if (aiDashboard.getCriticalRiskCount() > 0 || criticalAlertsCount >= 3) {
            healthStatus = "ATTENTION_REQUIRED";
        } else if (highRiskCount > 0 || openActions >= 5) {
            healthStatus = "WARNING";
        } else {
            healthStatus = "HEALTHY";
        }

        return MonitoringSummaryResponse.builder()
                .totalSuppliers((int) totalSuppliers)
                .activeSuppliers((int) activeSuppliers)
                .highRiskSuppliersCount(highRiskCount)
                .criticalAlertsCount(criticalAlertsCount)
                .openImprovementActionsCount((int) openActions)
                .inProgressImprovementActionsCount((int) inProgressActions)
                .completedImprovementActionsCount((int) completedActions)
                .recentEvaluationsCount((int) evaluationRepository.count())
                .unreadNotificationsCount((int) unreadNotifications)
                .systemHealthStatus(healthStatus)
                .averageEvaluationScore(round(aiDashboard.getAverageSystemRiskScore() != null ? 100.0 - aiDashboard.getAverageSystemRiskScore() : 85.0, 1))
                .criticalAlerts(aiDashboard.getCriticalAlerts())
                .topRiskWatchlist(aiDashboard.getTopRiskSuppliers())
                .urgentActions(urgentActions)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ImprovementActionResponse mapActionToResponse(SupplierImprovementAction a) {
        return ImprovementActionResponse.builder()
                .id(a.getId())
                .supplierId(a.getSupplier().getId())
                .supplierName(a.getSupplier().getName())
                .supplierCode(a.getSupplier().getSupplierCode())
                .title(a.getTitle())
                .description(a.getDescription())
                .priority(a.getPriority())
                .status(a.getStatus())
                .assignedUserId(a.getAssignedUser() != null ? a.getAssignedUser().getId() : null)
                .assignedUserName(a.getAssignedUser() != null ? a.getAssignedUser().getFullName() : null)
                .createdByUserId(a.getCreatedByUser() != null ? a.getCreatedByUser().getId() : null)
                .createdByUserName(a.getCreatedByUser() != null ? a.getCreatedByUser().getFullName() : null)
                .dueDate(a.getDueDate())
                .completedAt(a.getCompletedAt())
                .resolutionNotes(a.getResolutionNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private double round(double val, int places) {
        if (Double.isNaN(val) || Double.isInfinite(val)) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(val);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
