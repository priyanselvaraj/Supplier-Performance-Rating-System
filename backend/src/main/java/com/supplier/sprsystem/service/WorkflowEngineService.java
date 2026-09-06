package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ApprovalActionRequest;
import com.supplier.sprsystem.dto.request.ManualEscalationRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkflowEngineService {

    WorkflowInstanceResponse startWorkflow(
            WorkflowType type,
            String title,
            String resourceType,
            Long resourceId,
            User initiator,
            String metadata);

    ApprovalTaskResponse approveTask(Long taskId, User approver, ApprovalActionRequest request);

    ApprovalTaskResponse rejectTask(Long taskId, User rejector, ApprovalActionRequest request);

    WorkflowEscalationResponse escalateTask(Long taskId, User actor, ManualEscalationRequest request);

    int checkAndEscalateOverdueTasks();

    WorkflowSummaryResponse getWorkflowSummary(User currentUser);

    PaginatedResponse<WorkflowInstanceResponse> searchAndFilterWorkflows(
            WorkflowType type,
            WorkflowStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction);

    PaginatedResponse<ApprovalTaskResponse> getMyPendingTasks(User currentUser, int page, int size);

    PaginatedResponse<ApprovalTaskResponse> getMyApprovalHistory(User currentUser, TaskStatus status, int page, int size);

    WorkflowInstanceResponse getWorkflowInstanceById(Long id, User currentUser);

    List<WorkflowInstanceResponse> getWorkflowsByResource(String resourceType, Long resourceId);

    List<WorkflowEscalationResponse> getEscalations(int page, int size);

    ApprovalTaskResponse getApprovalTaskById(Long taskId, User currentUser);
}
