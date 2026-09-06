package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.ApprovalActionRequest;
import com.supplier.sprsystem.dto.request.ManualEscalationRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.NotificationService;
import com.supplier.sprsystem.service.WorkflowEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowEngineServiceImpl.class);

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final ApprovalTaskRepository taskRepository;
    private final WorkflowEscalationRepository escalationRepository;
    private final WorkflowAuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SupplierProfileUpdateRequestRepository profileUpdateRequestRepository;
    private final SupplierDocumentRepository documentRepository;
    private final SupplierRepository supplierRepository;

    public WorkflowEngineServiceImpl(WorkflowDefinitionRepository definitionRepository,
                                    WorkflowStepRepository stepRepository,
                                    WorkflowInstanceRepository instanceRepository,
                                    ApprovalTaskRepository taskRepository,
                                    WorkflowEscalationRepository escalationRepository,
                                    WorkflowAuditLogRepository auditLogRepository,
                                    UserRepository userRepository,
                                    NotificationService notificationService,
                                    @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierProfileUpdateRequestRepository profileUpdateRequestRepository,
                                    @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierDocumentRepository documentRepository,
                                    @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierRepository supplierRepository) {
        this.definitionRepository = definitionRepository;
        this.stepRepository = stepRepository;
        this.instanceRepository = instanceRepository;
        this.taskRepository = taskRepository;
        this.escalationRepository = escalationRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.profileUpdateRequestRepository = profileUpdateRequestRepository;
        this.documentRepository = documentRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public WorkflowInstanceResponse startWorkflow(
            WorkflowType type,
            String title,
            String resourceType,
            Long resourceId,
            User initiator,
            String metadata) {

        WorkflowDefinition definition = definitionRepository.findByWorkflowTypeAndActiveTrue(type)
                .orElseGet(() -> createDefaultDefinition(type));

        List<WorkflowStep> steps = definition.getSteps();
        if (steps == null || steps.isEmpty()) {
            throw new BadRequestException("No steps defined for workflow type: " + type);
        }

        steps.sort(Comparator.comparingInt(WorkflowStep::getStepOrder));
        WorkflowStep firstStep = steps.get(0);

        WorkflowInstance instance = WorkflowInstance.builder()
                .workflowDefinition(definition)
                .workflowType(type)
                .title(title != null ? title.trim() : (type.getDisplayName() + " Request #" + resourceId))
                .relatedResourceType(resourceType)
                .relatedResourceId(resourceId)
                .status(WorkflowStatus.IN_PROGRESS)
                .currentStepOrder(1)
                .totalSteps(steps.size())
                .initiatedBy(initiator)
                .startedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();

        WorkflowInstance savedInstance = instanceRepository.save(instance);

        // Create initial ApprovalTask
        LocalDateTime now = LocalDateTime.now();
        ApprovalTask initialTask = ApprovalTask.builder()
                .workflowInstance(savedInstance)
                .workflowStep(firstStep)
                .stepOrder(firstStep.getStepOrder())
                .stepName(firstStep.getStepName())
                .requiredRole(firstStep.getRequiredRole())
                .status(TaskStatus.PENDING)
                .assignedAt(now)
                .dueAt(now.plusHours(firstStep.getSlaHours()))
                .build();

        ApprovalTask savedTask = taskRepository.save(initialTask);
        savedInstance.addTask(savedTask);

        // Record Audit Logs
        recordAuditLog(savedInstance, "WORKFLOW_CREATED", initiator,
                "Workflow initiated by " + initiator.getFullName() + " (" + type.getDisplayName() + ")", null);
        recordAuditLog(savedInstance, "STEP_STARTED", initiator,
                "Step 1 started: " + firstStep.getStepName() + " assigned to " + firstStep.getRequiredRole().name(), null);

        // Broadcast notification to required role
        notificationService.broadcastToRole(
                firstStep.getRequiredRole().name(),
                "New Approval Request: " + savedInstance.getTitle(),
                "A new approval task '" + firstStep.getStepName() + "' requires your review (SLA: " + firstStep.getSlaHours() + "h).",
                NotificationType.WORKFLOW,
                NotificationPriority.MEDIUM,
                "WORKFLOW",
                savedInstance.getId()
        );

        return mapToInstanceResponse(savedInstance);
    }

    @Override
    @Transactional
    public ApprovalTaskResponse approveTask(Long taskId, User approver, ApprovalActionRequest request) {
        ApprovalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalTask", "id", taskId));

        WorkflowInstance instance = task.getWorkflowInstance();
        validateApproverAuthority(task, approver);

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Task is already in " + task.getStatus() + " status.");
        }

        LocalDateTime now = LocalDateTime.now();
        task.setStatus(TaskStatus.APPROVED);
        task.setActionedBy(approver);
        task.setActionedAt(now);
        task.setComments(request != null ? request.getComments() : null);
        ApprovalTask savedTask = taskRepository.save(task);

        recordAuditLog(instance, "TASK_APPROVED", approver,
                "Step " + task.getStepOrder() + " (" + task.getStepName() + ") approved by " + approver.getFullName(),
                task.getComments());

        // Check if next step exists
        WorkflowDefinition definition = instance.getWorkflowDefinition();
        List<WorkflowStep> steps = definition.getSteps();
        steps.sort(Comparator.comparingInt(WorkflowStep::getStepOrder));

        int nextOrder = task.getStepOrder() + 1;
        Optional<WorkflowStep> nextStepOpt = steps.stream().filter(s -> s.getStepOrder() == nextOrder).findFirst();

        if (nextStepOpt.isPresent()) {
            WorkflowStep nextStep = nextStepOpt.get();
            instance.setCurrentStepOrder(nextOrder);
            instance.setStatus(WorkflowStatus.IN_PROGRESS);

            ApprovalTask nextTask = ApprovalTask.builder()
                    .workflowInstance(instance)
                    .workflowStep(nextStep)
                    .stepOrder(nextStep.getStepOrder())
                    .stepName(nextStep.getStepName())
                    .requiredRole(nextStep.getRequiredRole())
                    .status(TaskStatus.PENDING)
                    .assignedAt(now)
                    .dueAt(now.plusHours(nextStep.getSlaHours()))
                    .build();

            ApprovalTask savedNextTask = taskRepository.save(nextTask);
            instance.addTask(savedNextTask);
            instanceRepository.save(instance);

            recordAuditLog(instance, "STEP_STARTED", approver,
                    "Step " + nextStep.getStepOrder() + " started: " + nextStep.getStepName() + " assigned to " + nextStep.getRequiredRole().name(), null);

            notificationService.broadcastToRole(
                    nextStep.getRequiredRole().name(),
                    "Approval Required: " + instance.getTitle(),
                    "Step " + nextOrder + " (" + nextStep.getStepName() + ") is now pending your review.",
                    NotificationType.WORKFLOW,
                    NotificationPriority.MEDIUM,
                    "WORKFLOW",
                    instance.getId()
            );
        } else {
            // Final step approved -> Complete workflow
            instance.setStatus(WorkflowStatus.APPROVED);
            instance.setCompletedAt(now);
            instanceRepository.save(instance);

            recordAuditLog(instance, "WORKFLOW_COMPLETED", approver,
                    "Workflow successfully approved and completed all " + instance.getTotalSteps() + " steps.", null);

            // Notify initiator
            if (instance.getInitiatedBy() != null) {
                notificationService.createNotification(
                        instance.getInitiatedBy().getId(),
                        "Workflow Approved: " + instance.getTitle(),
                        "Your request has received full approval and is now complete.",
                        NotificationType.WORKFLOW,
                        NotificationPriority.HIGH,
                        "WORKFLOW",
                        instance.getId()
                );
            }

            // Execute post-approval resource hook
            executePostApprovalAction(instance, approver);
        }

        return mapToTaskResponse(savedTask);
    }

    @Override
    @Transactional
    public ApprovalTaskResponse rejectTask(Long taskId, User rejector, ApprovalActionRequest request) {
        ApprovalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalTask", "id", taskId));

        WorkflowInstance instance = task.getWorkflowInstance();
        validateApproverAuthority(task, rejector);

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Task is already in " + task.getStatus() + " status.");
        }

        String reason = (request != null && request.getRejectionReason() != null && !request.getRejectionReason().trim().isEmpty())
                ? request.getRejectionReason().trim()
                : (request != null ? request.getComments() : "Request rejected by reviewer");

        LocalDateTime now = LocalDateTime.now();
        task.setStatus(TaskStatus.REJECTED);
        task.setActionedBy(rejector);
        task.setActionedAt(now);
        task.setComments(reason);
        ApprovalTask savedTask = taskRepository.save(task);

        instance.setStatus(WorkflowStatus.REJECTED);
        instance.setRejectionReason(reason);
        instance.setCompletedAt(now);
        instanceRepository.save(instance);

        recordAuditLog(instance, "TASK_REJECTED", rejector,
                "Step " + task.getStepOrder() + " (" + task.getStepName() + ") rejected by " + rejector.getFullName() + ". Reason: " + reason,
                reason);
        recordAuditLog(instance, "WORKFLOW_REJECTED", rejector,
                "Workflow was rejected at Step " + task.getStepOrder() + ". Reason: " + reason,
                reason);

        // Notify initiator
        if (instance.getInitiatedBy() != null) {
            notificationService.createNotification(
                    instance.getInitiatedBy().getId(),
                    "Workflow Rejected: " + instance.getTitle(),
                    "Your request was rejected. Reason: " + reason,
                    NotificationType.WORKFLOW,
                    NotificationPriority.HIGH,
                    "WORKFLOW",
                    instance.getId()
            );
        }

        // Execute post-rejection resource hook
        executePostRejectionAction(instance, rejector, reason);

        return mapToTaskResponse(savedTask);
    }

    @Override
    @Transactional
    public WorkflowEscalationResponse escalateTask(Long taskId, User actor, ManualEscalationRequest request) {
        ApprovalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalTask", "id", taskId));

        WorkflowInstance instance = task.getWorkflowInstance();
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Cannot escalate a task that is not in PENDING status.");
        }

        if (escalationRepository.existsByApprovalTaskId(taskId)) {
            throw new BadRequestException("Task has already been escalated.");
        }

        ERole targetRole = request != null && request.getTargetRole() != null ? request.getTargetRole() : ERole.ROLE_ADMIN;
        String reason = request != null && request.getReason() != null ? request.getReason().trim() : "Task overdue or escalated manually";

        task.setStatus(TaskStatus.ESCALATED);
        task.setEscalated(true);
        task.setRequiredRole(targetRole);
        taskRepository.save(task);

        instance.setStatus(WorkflowStatus.ESCALATED);
        instanceRepository.save(instance);

        User targetUser = null;
        if (request != null && request.getTargetUserId() != null) {
            targetUser = userRepository.findById(request.getTargetUserId()).orElse(null);
        }

        WorkflowEscalation escalation = WorkflowEscalation.builder()
                .workflowInstance(instance)
                .approvalTask(task)
                .escalationLevel(1)
                .reason(reason)
                .escalatedFromRole(task.getRequiredRole())
                .escalatedToRole(targetRole)
                .escalatedToUser(targetUser)
                .escalatedAt(LocalDateTime.now())
                .build();

        WorkflowEscalation savedEscalation = escalationRepository.save(escalation);
        instance.addEscalation(savedEscalation);

        recordAuditLog(instance, "TASK_ESCALATED", actor,
                "Task escalated from " + task.getRequiredRole().name() + " to " + targetRole.name() + ". Reason: " + reason,
                reason);

        // Broadcast escalation notification to Admin
        notificationService.broadcastToRole(
                targetRole.name(),
                "URGENT ESCALATION: " + instance.getTitle(),
                "Task '" + task.getStepName() + "' has been escalated. Reason: " + reason,
                NotificationType.ESCALATION,
                NotificationPriority.CRITICAL,
                "WORKFLOW",
                instance.getId()
        );

        return mapToEscalationResponse(savedEscalation);
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public int checkAndEscalateOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<ApprovalTask> overdueTasks = taskRepository.findOverduePendingTasks(now);
        int escalatedCount = 0;

        for (ApprovalTask task : overdueTasks) {
            WorkflowStep step = task.getWorkflowStep();
            if (step != null && !step.isAutoEscalate()) {
                continue; // Auto-escalation disabled for this step
            }

            if (!escalationRepository.existsByApprovalTaskId(task.getId())) {
                try {
                    ERole targetRole = (step != null && step.getEscalationRole() != null) ? step.getEscalationRole() : ERole.ROLE_ADMIN;
                    Duration overTime = Duration.between(task.getDueAt(), now);
                    long hoursOver = Math.max(1, overTime.toHours());

                    String reason = "SLA deadline (" + (step != null ? step.getSlaHours() : 24) + "h) breached by " + hoursOver + " hour(s). Auto-escalated to " + targetRole.name().replace("ROLE_", "") + ".";

                    ManualEscalationRequest escReq = new ManualEscalationRequest(reason, targetRole, null);
                    escalateTask(task.getId(), null, escReq);
                    escalatedCount++;
                    logger.info("Auto-escalated overdue task ID {} on workflow ID {}", task.getId(), task.getWorkflowInstance().getId());
                } catch (Exception e) {
                    logger.error("Failed to auto-escalate task ID {}: {}", task.getId(), e.getMessage());
                }
            }
        }
        return escalatedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowSummaryResponse getWorkflowSummary(User currentUser) {
        long total = instanceRepository.count();
        long pending = taskRepository.count();
        long inProgress = instanceRepository.findByStatus(WorkflowStatus.IN_PROGRESS).size();
        long approved = instanceRepository.countByStatus(WorkflowStatus.APPROVED);
        long rejected = instanceRepository.countByStatus(WorkflowStatus.REJECTED);
        long overdue = instanceRepository.countOverdueWorkflows();
        long escalated = instanceRepository.countEscalatedWorkflows();

        List<ERole> userRoles = currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        long myPending = taskRepository.countPendingTasksForUser(currentUser.getId(), userRoles);

        return WorkflowSummaryResponse.builder()
                .totalWorkflows(total)
                .pendingApprovals(inProgress + instanceRepository.countByStatus(WorkflowStatus.PENDING))
                .inProgressWorkflows(inProgress)
                .approvedWorkflows(approved)
                .rejectedWorkflows(rejected)
                .overdueTasks(overdue)
                .escalatedWorkflows(escalated)
                .myPendingTasks(myPending)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<WorkflowInstanceResponse> searchAndFilterWorkflows(
            WorkflowType type,
            WorkflowStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String keyword,
            int page,
            int size,
            String sortBy,
            String direction) {

        String cleanSort = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : "startedAt";
        Sort.Direction sortDir = (direction != null && direction.equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(sortDir, cleanSort));

        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<WorkflowInstance> pageResult = instanceRepository.searchAndFilter(type, status, startDate, endDate, cleanKeyword, pageable);

        return PaginatedResponse.fromPage(pageResult.map(this::mapToInstanceResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ApprovalTaskResponse> getMyPendingTasks(User currentUser, int page, int size) {
        List<ERole> userRoles = currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.ASC, "dueAt"));

        Page<ApprovalTask> pageResult = taskRepository.findPendingTasksForUser(currentUser.getId(), userRoles, pageable);
        return PaginatedResponse.fromPage(pageResult.map(this::mapToTaskResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ApprovalTaskResponse> getMyApprovalHistory(User currentUser, TaskStatus status, int page, int size) {
        List<ERole> userRoles = currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "assignedAt"));

        Page<ApprovalTask> pageResult = taskRepository.findUserApprovalHistory(currentUser.getId(), userRoles, status, pageable);
        return PaginatedResponse.fromPage(pageResult.map(this::mapToTaskResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowInstanceResponse getWorkflowInstanceById(Long id, User currentUser) {
        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowInstance", "id", id));
        return mapToInstanceResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowInstanceResponse> getWorkflowsByResource(String resourceType, Long resourceId) {
        return instanceRepository.findByRelatedResourceTypeAndRelatedResourceId(resourceType, resourceId).stream()
                .map(this::mapToInstanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowEscalationResponse> getEscalations(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "escalatedAt"));
        return escalationRepository.findAllByOrderByEscalatedAtDesc(pageable).getContent().stream()
                .map(this::mapToEscalationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalTaskResponse getApprovalTaskById(Long taskId, User currentUser) {
        ApprovalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalTask", "id", taskId));
        return mapToTaskResponse(task);
    }

    private void validateApproverAuthority(ApprovalTask task, User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
        boolean hasRequiredRole = user.getRoles().stream().anyMatch(r -> r.getName() == task.getRequiredRole());
        boolean isDirectAssignee = task.getAssignedUser() != null && task.getAssignedUser().getId().equals(user.getId());

        if (!isAdmin && !hasRequiredRole && !isDirectAssignee) {
            throw new UnauthorizedException("You do not have the required role (" + task.getRequiredRole().name() + ") to action this task.");
        }
    }

    private void recordAuditLog(WorkflowInstance instance, String eventType, User user, String description, String details) {
        WorkflowAuditLog log = WorkflowAuditLog.builder()
                .workflowInstance(instance)
                .eventType(eventType)
                .performedBy(user)
                .timestamp(LocalDateTime.now())
                .description(description)
                .details(details)
                .build();
        auditLogRepository.save(log);
        instance.addAuditLog(log);
    }

    private void executePostApprovalAction(WorkflowInstance instance, User approver) {
        try {
            if (instance.getWorkflowType() == WorkflowType.SUPPLIER_PROFILE_UPDATE && profileUpdateRequestRepository != null && supplierRepository != null) {
                SupplierProfileUpdateRequest req = profileUpdateRequestRepository.findById(instance.getRelatedResourceId()).orElse(null);
                if (req != null) {
                    req.setStatus(UpdateRequestStatus.APPROVED);
                    req.setReviewedBy(approver);
                    req.setReviewedAt(LocalDateTime.now());
                    req.setReviewerNotes("Approved via multi-level workflow #" + instance.getId());
                    profileUpdateRequestRepository.save(req);

                    // Apply updates to Supplier
                    Supplier s = req.getSupplier();
                    if (s != null) {
                        if (req.getContactPerson() != null) s.setContactPerson(req.getContactPerson());
                        if (req.getPhone() != null) s.setPhone(req.getPhone());
                        if (req.getEmail() != null) s.setEmail(req.getEmail());
                        if (req.getAddress() != null) s.setAddress(req.getAddress());
                        if (req.getWebsite() != null) s.setWebsite(req.getWebsite());
                        if (req.getCity() != null) s.setCity(req.getCity());
                        if (req.getState() != null) s.setState(req.getState());
                        if (req.getCountry() != null) s.setCountry(req.getCountry());
                        supplierRepository.save(s);
                    }
                }
            } else if (instance.getWorkflowType() == WorkflowType.SUPPLIER_DOCUMENT_REVIEW && documentRepository != null) {
                SupplierDocument doc = documentRepository.findById(instance.getRelatedResourceId()).orElse(null);
                if (doc != null) {
                    doc.setStatus(DocumentStatus.ACTIVE);
                    doc.setNotes("Approved via workflow #" + instance.getId());
                    documentRepository.save(doc);
                }
            }
        } catch (Exception e) {
            logger.error("Error executing post-approval action for workflow {}: {}", instance.getId(), e.getMessage());
        }
    }

    private void executePostRejectionAction(WorkflowInstance instance, User rejector, String reason) {
        try {
            if (instance.getWorkflowType() == WorkflowType.SUPPLIER_PROFILE_UPDATE && profileUpdateRequestRepository != null) {
                SupplierProfileUpdateRequest req = profileUpdateRequestRepository.findById(instance.getRelatedResourceId()).orElse(null);
                if (req != null) {
                    req.setStatus(UpdateRequestStatus.REJECTED);
                    req.setReviewedBy(rejector);
                    req.setReviewedAt(LocalDateTime.now());
                    req.setReviewerNotes(reason);
                    profileUpdateRequestRepository.save(req);
                }
            } else if (instance.getWorkflowType() == WorkflowType.SUPPLIER_DOCUMENT_REVIEW && documentRepository != null) {
                SupplierDocument doc = documentRepository.findById(instance.getRelatedResourceId()).orElse(null);
                if (doc != null) {
                    doc.setStatus(DocumentStatus.REJECTED);
                    doc.setNotes("Rejected via workflow #" + instance.getId() + ": " + reason);
                    documentRepository.save(doc);
                }
            }
        } catch (Exception e) {
            logger.error("Error executing post-rejection action for workflow {}: {}", instance.getId(), e.getMessage());
        }
    }

    private WorkflowDefinition createDefaultDefinition(WorkflowType type) {
        WorkflowDefinition def = WorkflowDefinition.builder()
                .name(type.getDisplayName() + " Workflow")
                .workflowType(type)
                .description("Default standard multi-level approval workflow for " + type.getDisplayName())
                .active(true)
                .build();

        if (type == WorkflowType.SUPPLIER_PROFILE_UPDATE) {
            WorkflowStep step1 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(1)
                    .stepName("Manager Verification")
                    .requiredRole(ERole.ROLE_MANAGER)
                    .slaHours(24)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Verify updated vendor business contact information and corporate address validity.")
                    .build();

            WorkflowStep step2 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(2)
                    .stepName("Admin Final Sign-Off")
                    .requiredRole(ERole.ROLE_ADMIN)
                    .slaHours(48)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Authorize vendor profile change into the master ERP records.")
                    .build();

            def.addStep(step1);
            def.addStep(step2);
        } else if (type == WorkflowType.SUPPLIER_DOCUMENT_REVIEW) {
            WorkflowStep step1 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(1)
                    .stepName("Quality & Compliance Review")
                    .requiredRole(ERole.ROLE_MANAGER)
                    .slaHours(48)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Review uploaded ISO / RoHS / regulatory compliance certificates.")
                    .build();
            def.addStep(step1);
        } else if (type == WorkflowType.EVALUATION_APPROVAL) {
            WorkflowStep step1 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(1)
                    .stepName("Procurement Lead Review")
                    .requiredRole(ERole.ROLE_MANAGER)
                    .slaHours(24)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Audit criteria evaluation scores and ratings computation.")
                    .build();

            WorkflowStep step2 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(2)
                    .stepName("Admin Rating Finalization")
                    .requiredRole(ERole.ROLE_ADMIN)
                    .slaHours(48)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Finalize official rating score on the vendor record.")
                    .build();

            def.addStep(step1);
            def.addStep(step2);
        } else if (type == WorkflowType.IMPROVEMENT_ACTION_CLOSURE) {
            WorkflowStep step1 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(1)
                    .stepName("CAP Countermeasure Verification")
                    .requiredRole(ERole.ROLE_MANAGER)
                    .slaHours(48)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Verify supplier corrective action resolution notes and effectiveness.")
                    .build();
            def.addStep(step1);
        } else {
            WorkflowStep step1 = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(1)
                    .stepName("Admin Approval")
                    .requiredRole(ERole.ROLE_ADMIN)
                    .slaHours(24)
                    .escalationRole(ERole.ROLE_ADMIN)
                    .autoEscalate(true)
                    .instructions("Standard executive approval.")
                    .build();
            def.addStep(step1);
        }

        return definitionRepository.save(def);
    }

    public WorkflowInstanceResponse mapToInstanceResponse(WorkflowInstance inst) {
        List<ApprovalTaskResponse> taskResponses = inst.getTasks() != null ? inst.getTasks().stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList()) : new ArrayList<>();

        List<WorkflowEscalationResponse> escResponses = inst.getEscalations() != null ? inst.getEscalations().stream()
                .map(this::mapToEscalationResponse)
                .collect(Collectors.toList()) : new ArrayList<>();

        List<WorkflowAuditLogResponse> auditResponses = inst.getAuditLogs() != null ? inst.getAuditLogs().stream()
                .map(this::mapToAuditResponse)
                .collect(Collectors.toList()) : new ArrayList<>();

        boolean isOverdue = taskResponses.stream().anyMatch(ApprovalTaskResponse::isOverdue);
        boolean isEscalated = inst.getStatus() == WorkflowStatus.ESCALATED || !escResponses.isEmpty();

        return WorkflowInstanceResponse.builder()
                .id(inst.getId())
                .workflowDefinitionId(inst.getWorkflowDefinition() != null ? inst.getWorkflowDefinition().getId() : null)
                .workflowDefinitionName(inst.getWorkflowDefinition() != null ? inst.getWorkflowDefinition().getName() : null)
                .workflowType(inst.getWorkflowType())
                .workflowTypeDisplayName(inst.getWorkflowType() != null ? inst.getWorkflowType().getDisplayName() : null)
                .title(inst.getTitle())
                .relatedResourceType(inst.getRelatedResourceType())
                .relatedResourceId(inst.getRelatedResourceId())
                .status(inst.getStatus())
                .statusDisplayName(inst.getStatus() != null ? inst.getStatus().getDisplayName() : null)
                .currentStepOrder(inst.getCurrentStepOrder())
                .totalSteps(inst.getTotalSteps())
                .initiatedById(inst.getInitiatedBy() != null ? inst.getInitiatedBy().getId() : null)
                .initiatedByName(inst.getInitiatedBy() != null ? inst.getInitiatedBy().getFullName() : null)
                .startedAt(inst.getStartedAt())
                .completedAt(inst.getCompletedAt())
                .rejectionReason(inst.getRejectionReason())
                .metadata(inst.getMetadata())
                .isOverdue(isOverdue)
                .isEscalated(isEscalated)
                .tasks(taskResponses)
                .escalations(escResponses)
                .auditLogs(auditResponses)
                .build();
    }

    public ApprovalTaskResponse mapToTaskResponse(ApprovalTask task) {
        boolean isOverdue = task.isOverdue();
        Long hoursRemaining = null;
        if (task.getDueAt() != null) {
            Duration dur = Duration.between(LocalDateTime.now(), task.getDueAt());
            hoursRemaining = dur.toHours();
        }

        WorkflowInstance inst = task.getWorkflowInstance();

        return ApprovalTaskResponse.builder()
                .id(task.getId())
                .workflowInstanceId(inst != null ? inst.getId() : null)
                .workflowType(inst != null ? inst.getWorkflowType() : null)
                .workflowTitle(inst != null ? inst.getTitle() : null)
                .relatedResourceType(inst != null ? inst.getRelatedResourceType() : null)
                .relatedResourceId(inst != null ? inst.getRelatedResourceId() : null)
                .workflowStepId(task.getWorkflowStep() != null ? task.getWorkflowStep().getId() : null)
                .stepOrder(task.getStepOrder())
                .stepName(task.getStepName())
                .requiredRole(task.getRequiredRole())
                .requiredRoleDisplayName(task.getRequiredRole() != null ? task.getRequiredRole().name().replace("ROLE_", "") : null)
                .assignedUserId(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null)
                .assignedUserName(task.getAssignedUser() != null ? task.getAssignedUser().getFullName() : null)
                .actionedById(task.getActionedBy() != null ? task.getActionedBy().getId() : null)
                .actionedByName(task.getActionedBy() != null ? task.getActionedBy().getFullName() : null)
                .status(task.getStatus())
                .statusDisplayName(task.getStatus() != null ? task.getStatus().getDisplayName() : null)
                .assignedAt(task.getAssignedAt())
                .dueAt(task.getDueAt())
                .actionedAt(task.getActionedAt())
                .comments(task.getComments())
                .isOverdue(isOverdue)
                .escalated(task.isEscalated())
                .hoursRemaining(hoursRemaining)
                .build();
    }

    public WorkflowEscalationResponse mapToEscalationResponse(WorkflowEscalation esc) {
        return WorkflowEscalationResponse.builder()
                .id(esc.getId())
                .workflowInstanceId(esc.getWorkflowInstance() != null ? esc.getWorkflowInstance().getId() : null)
                .approvalTaskId(esc.getApprovalTask() != null ? esc.getApprovalTask().getId() : null)
                .escalationLevel(esc.getEscalationLevel())
                .reason(esc.getReason())
                .escalatedFromRole(esc.getEscalatedFromRole())
                .escalatedFromRoleDisplayName(esc.getEscalatedFromRole() != null ? esc.getEscalatedFromRole().name().replace("ROLE_", "") : null)
                .escalatedToRole(esc.getEscalatedToRole())
                .escalatedToRoleDisplayName(esc.getEscalatedToRole() != null ? esc.getEscalatedToRole().name().replace("ROLE_", "") : null)
                .escalatedToUserId(esc.getEscalatedToUser() != null ? esc.getEscalatedToUser().getId() : null)
                .escalatedToUserName(esc.getEscalatedToUser() != null ? esc.getEscalatedToUser().getFullName() : null)
                .escalatedAt(esc.getEscalatedAt())
                .resolved(esc.isResolved())
                .resolvedAt(esc.getResolvedAt())
                .resolutionNotes(esc.getResolutionNotes())
                .build();
    }

    public WorkflowAuditLogResponse mapToAuditResponse(WorkflowAuditLog log) {
        return WorkflowAuditLogResponse.builder()
                .id(log.getId())
                .workflowInstanceId(log.getWorkflowInstance() != null ? log.getWorkflowInstance().getId() : null)
                .eventType(log.getEventType())
                .performedById(log.getPerformedBy() != null ? log.getPerformedBy().getId() : null)
                .performedByName(log.getPerformedBy() != null ? log.getPerformedBy().getFullName() : "System Automation")
                .timestamp(log.getTimestamp())
                .description(log.getDescription())
                .details(log.getDetails())
                .build();
    }
}
