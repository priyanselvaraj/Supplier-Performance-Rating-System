package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ApprovalActionRequest;
import com.supplier.sprsystem.dto.request.ManualEscalationRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.impl.WorkflowEngineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WorkflowEngineServiceTest {

    @Mock
    private WorkflowDefinitionRepository definitionRepository;

    @Mock
    private WorkflowStepRepository stepRepository;

    @Mock
    private WorkflowInstanceRepository instanceRepository;

    @Mock
    private ApprovalTaskRepository taskRepository;

    @Mock
    private WorkflowEscalationRepository escalationRepository;

    @Mock
    private WorkflowAuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SupplierProfileUpdateRequestRepository profileUpdateRequestRepository;

    @Mock
    private SupplierDocumentRepository documentRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private WorkflowEngineServiceImpl workflowEngineService;

    private User managerUser;
    private User adminUser;
    private User supplierUser;
    private WorkflowDefinition sampleDefinition;
    private WorkflowStep step1;
    private WorkflowStep step2;
    private WorkflowInstance sampleInstance;
    private ApprovalTask sampleTask;

    @BeforeEach
    void setUp() {
        Role roleManager = Role.builder().name(ERole.ROLE_MANAGER).build();
        Role roleAdmin = Role.builder().name(ERole.ROLE_ADMIN).build();
        Role roleSupplier = Role.builder().name(ERole.ROLE_SUPPLIER).build();

        managerUser = User.builder()
                .id(2L)
                .username("manager")
                .fullName("Procurement Manager")
                .roles(Set.of(roleManager))
                .build();

        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .fullName("System Administrator")
                .roles(Set.of(roleAdmin, roleManager))
                .build();

        supplierUser = User.builder()
                .id(3L)
                .username("supplier_apex")
                .fullName("Apex Supplier Rep")
                .roles(Set.of(roleSupplier))
                .build();

        sampleDefinition = WorkflowDefinition.builder()
                .id(10L)
                .name("Supplier Profile Update Review")
                .workflowType(WorkflowType.SUPPLIER_PROFILE_UPDATE)
                .active(true)
                .build();

        step1 = WorkflowStep.builder()
                .id(101L)
                .workflowDefinition(sampleDefinition)
                .stepOrder(1)
                .stepName("Manager Review")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .build();

        step2 = WorkflowStep.builder()
                .id(102L)
                .workflowDefinition(sampleDefinition)
                .stepOrder(2)
                .stepName("Admin Final Approval")
                .requiredRole(ERole.ROLE_ADMIN)
                .slaHours(48)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(false)
                .build();

        sampleDefinition.setSteps(new ArrayList<>(List.of(step1, step2)));

        sampleInstance = WorkflowInstance.builder()
                .id(500L)
                .workflowDefinition(sampleDefinition)
                .workflowType(WorkflowType.SUPPLIER_PROFILE_UPDATE)
                .title("Profile Update: Apex Microelectronics")
                .relatedResourceType("PROFILE_UPDATE_REQUEST")
                .relatedResourceId(1L)
                .status(WorkflowStatus.IN_PROGRESS)
                .currentStepOrder(1)
                .totalSteps(2)
                .initiatedBy(supplierUser)
                .startedAt(LocalDateTime.now())
                .tasks(new ArrayList<>())
                .auditLogs(new ArrayList<>())
                .build();

        sampleTask = ApprovalTask.builder()
                .id(1001L)
                .workflowInstance(sampleInstance)
                .workflowStep(step1)
                .stepOrder(1)
                .stepName("Manager Review")
                .requiredRole(ERole.ROLE_MANAGER)
                .status(TaskStatus.PENDING)
                .assignedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusHours(24))
                .build();

        sampleInstance.addTask(sampleTask);
    }

    @Test
    @DisplayName("WF-01: startWorkflow should initialize instance, task, audit logs, and broadcast notification")
    void testStartWorkflow() {
        when(definitionRepository.findByWorkflowTypeAndActiveTrue(WorkflowType.SUPPLIER_PROFILE_UPDATE))
                .thenReturn(Optional.of(sampleDefinition));
        when(instanceRepository.save(any(WorkflowInstance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(taskRepository.save(any(ApprovalTask.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkflowInstanceResponse response = workflowEngineService.startWorkflow(
                WorkflowType.SUPPLIER_PROFILE_UPDATE,
                "Profile Update: Apex Microelectronics",
                "PROFILE_UPDATE_REQUEST",
                1L,
                supplierUser,
                "{}"
        );

        assertNotNull(response);
        assertEquals(WorkflowType.SUPPLIER_PROFILE_UPDATE, response.getWorkflowType());
        assertEquals(WorkflowStatus.IN_PROGRESS, response.getStatus());
        assertEquals(1, response.getCurrentStepOrder());
        assertEquals(2, response.getTotalSteps());

        verify(instanceRepository).save(any(WorkflowInstance.class));
        verify(taskRepository).save(any(ApprovalTask.class));
        verify(auditLogRepository, times(2)).save(any(WorkflowAuditLog.class));
        verify(notificationService).broadcastToRole(eq("ROLE_MANAGER"), anyString(), anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("WF-02: approveTask on intermediate step should transition to next step")
    void testApproveTask_IntermediateStep() {
        when(taskRepository.findById(1001L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(ApprovalTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setComments("Manager step looks good, proceeding to Admin.");

        ApprovalTaskResponse response = workflowEngineService.approveTask(1001L, managerUser, request);

        assertNotNull(response);
        assertEquals(TaskStatus.APPROVED, sampleTask.getStatus());
        assertEquals(managerUser, sampleTask.getActionedBy());
        assertEquals(2, sampleInstance.getCurrentStepOrder());
        assertEquals(WorkflowStatus.IN_PROGRESS, sampleInstance.getStatus());

        // Should create next task for Admin
        verify(taskRepository, times(2)).save(any(ApprovalTask.class));
        verify(auditLogRepository, times(2)).save(any(WorkflowAuditLog.class));
    }

    @Test
    @DisplayName("WF-03: approveTask on final step should complete workflow and execute resource update")
    void testApproveTask_FinalStep() {
        // Prepare step 2 task
        ApprovalTask finalTask = ApprovalTask.builder()
                .id(1002L)
                .workflowInstance(sampleInstance)
                .workflowStep(step2)
                .stepOrder(2)
                .stepName("Admin Final Approval")
                .requiredRole(ERole.ROLE_ADMIN)
                .status(TaskStatus.PENDING)
                .assignedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusHours(48))
                .build();
        sampleInstance.setCurrentStepOrder(2);
        sampleInstance.addTask(finalTask);

        when(taskRepository.findById(1002L)).thenReturn(Optional.of(finalTask));
        when(taskRepository.save(any(ApprovalTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setComments("Final Admin approval granted.");

        ApprovalTaskResponse response = workflowEngineService.approveTask(1002L, adminUser, request);

        assertNotNull(response);
        assertEquals(TaskStatus.APPROVED, finalTask.getStatus());
        assertEquals(WorkflowStatus.APPROVED, sampleInstance.getStatus());
        assertNotNull(sampleInstance.getCompletedAt());

        verify(instanceRepository).save(sampleInstance);
        verify(auditLogRepository, times(2)).save(any(WorkflowAuditLog.class));
    }

    @Test
    @DisplayName("WF-04: rejectTask should mark task REJECTED and workflow REJECTED")
    void testRejectTask() {
        when(taskRepository.findById(1001L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(ApprovalTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setRejectionReason("Incomplete tax certificates provided.");

        ApprovalTaskResponse response = workflowEngineService.rejectTask(1001L, managerUser, request);

        assertNotNull(response);
        assertEquals(TaskStatus.REJECTED, sampleTask.getStatus());
        assertEquals(WorkflowStatus.REJECTED, sampleInstance.getStatus());
        assertEquals("Incomplete tax certificates provided.", sampleInstance.getRejectionReason());

        verify(instanceRepository).save(sampleInstance);
        verify(auditLogRepository, times(2)).save(any(WorkflowAuditLog.class));
    }

    @Test
    @DisplayName("WF-05: escalateTask should create escalation record and assign to escalation role")
    void testEscalateTask() {
        when(taskRepository.findById(1001L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(ApprovalTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(escalationRepository.save(any(WorkflowEscalation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ManualEscalationRequest request = new ManualEscalationRequest();
        request.setReason("Manager is out of office; urgent approval needed.");

        WorkflowEscalationResponse response = workflowEngineService.escalateTask(1001L, adminUser, request);

        assertNotNull(response);
        assertEquals(TaskStatus.ESCALATED, sampleTask.getStatus());
        assertEquals(WorkflowStatus.ESCALATED, sampleInstance.getStatus());
        assertEquals(ERole.ROLE_ADMIN, sampleTask.getRequiredRole());

        verify(escalationRepository).save(any(WorkflowEscalation.class));
        verify(auditLogRepository).save(any(WorkflowAuditLog.class));
    }

    @Test
    @DisplayName("WF-06: checkAndEscalateOverdueTasks should scan and escalate overdue pending tasks")
    void testCheckAndEscalateOverdueTasks() {
        sampleTask.setDueAt(LocalDateTime.now().minusHours(5)); // overdue

        when(taskRepository.findOverduePendingTasks(any(LocalDateTime.class)))
                .thenReturn(List.of(sampleTask));
        when(taskRepository.findById(1001L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(ApprovalTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(instanceRepository.save(any(WorkflowInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(escalationRepository.save(any(WorkflowEscalation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int count = workflowEngineService.checkAndEscalateOverdueTasks();

        assertEquals(1, count);
        assertEquals(TaskStatus.ESCALATED, sampleTask.getStatus());
        assertEquals(WorkflowStatus.ESCALATED, sampleInstance.getStatus());

        verify(escalationRepository).save(any(WorkflowEscalation.class));
        verify(auditLogRepository).save(any(WorkflowAuditLog.class));
    }

    @Test
    @DisplayName("WF-07: validateApproverAuthority should throw UnauthorizedException for unauthorized role")
    void testValidateApproverAuthority_Unauthorized() {
        when(taskRepository.findById(1001L)).thenReturn(Optional.of(sampleTask));

        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setComments("Trying to approve");

        // Supplier attempting to approve manager task
        assertThrows(UnauthorizedException.class, () ->
                workflowEngineService.approveTask(1001L, supplierUser, request)
        );
    }

    @Test
    @DisplayName("WF-08: getWorkflowSummary should return accurate aggregated metrics")
    void testGetWorkflowSummary() {
        when(instanceRepository.count()).thenReturn(10L);
        when(instanceRepository.countByStatus(WorkflowStatus.PENDING)).thenReturn(1L);
        when(instanceRepository.findByStatus(WorkflowStatus.IN_PROGRESS)).thenReturn(List.of(sampleInstance, sampleInstance, sampleInstance, sampleInstance));
        when(instanceRepository.countByStatus(WorkflowStatus.APPROVED)).thenReturn(3L);
        when(instanceRepository.countByStatus(WorkflowStatus.REJECTED)).thenReturn(1L);
        when(instanceRepository.countOverdueWorkflows()).thenReturn(1L);
        when(instanceRepository.countEscalatedWorkflows()).thenReturn(1L);
        when(taskRepository.countPendingTasksForUser(any(), any())).thenReturn(1L);

        WorkflowSummaryResponse summary = workflowEngineService.getWorkflowSummary(adminUser);

        assertNotNull(summary);
        assertEquals(10L, summary.getTotalWorkflows());
        assertEquals(5L, summary.getPendingApprovals()); // 4 (IN_PROGRESS) + 1 (PENDING)
        assertEquals(3L, summary.getApprovedWorkflows());
        assertEquals(1L, summary.getRejectedWorkflows());
        assertEquals(1L, summary.getEscalatedWorkflows());
        assertEquals(1L, summary.getMyPendingTasks());
        assertEquals(1L, summary.getOverdueTasks());
    }
}
