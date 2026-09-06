package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.ApprovalActionRequest;
import com.supplier.sprsystem.dto.request.WorkflowDefinitionRequest;
import com.supplier.sprsystem.dto.request.WorkflowStepRequest;
import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.WorkflowType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class WorkflowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("WF-IT-01: Admin can retrieve workflows list with pagination and summary")
    void testGetWorkflowsAndSummary() throws Exception {
        mockMvc.perform(get("/api/v1/workflows")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));

        mockMvc.perform(get("/api/v1/workflows/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalWorkflows", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("WF-IT-02: Manager can retrieve assigned pending approvals and history")
    void testGetMyPendingApprovals() throws Exception {
        mockMvc.perform(get("/api/v1/approvals/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));

        mockMvc.perform(get("/api/v1/approvals/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("WF-IT-03: Admin can view workflow details with steps and audit trail")
    void testGetWorkflowDetails() throws Exception {
        mockMvc.perform(get("/api/v1/workflows/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.workflowType", notNullValue()))
                .andExpect(jsonPath("$.data.tasks", notNullValue()))
                .andExpect(jsonPath("$.data.auditLogs", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("WF-IT-04: Manager can approve pending task")
    void testApprovePendingTask() throws Exception {
        ApprovalActionRequest request = new ApprovalActionRequest();
        request.setComments("Manager verified profile changes; looks good.");

        mockMvc.perform(post("/api/v1/approvals/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("APPROVED")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("WF-IT-05: Admin can trigger overdue SLA scanner")
    void testCheckOverdueTasks() throws Exception {
        mockMvc.perform(post("/api/v1/escalations/check-overdue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.escalatedCount", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("WF-IT-06: Admin can list and toggle workflow definitions")
    void testAdminWorkflowDefinitions() throws Exception {
        mockMvc.perform(get("/api/v1/admin/workflows/definitions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(patch("/api/v1/admin/workflows/definitions/1/toggle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("WF-IT-07: Supplier cannot access admin workflow configuration (403 Forbidden)")
    void testSupplierForbiddenAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/workflows/definitions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
