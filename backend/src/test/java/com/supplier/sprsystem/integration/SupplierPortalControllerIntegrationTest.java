package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.ProfileUpdateRequestReviewDto;
import com.supplier.sprsystem.dto.request.SupplierCommunicationCreateRequest;
import com.supplier.sprsystem.dto.request.SupplierProfileUpdateSubmitRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SupplierPortalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-01: Supplier can retrieve self-service dashboard")
    void testGetSupplierDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierName", notNullValue()))
                .andExpect(jsonPath("$.data.overallRating", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-02: Supplier can view company profile")
    void testGetSupplierProfile() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", notNullValue()))
                .andExpect(jsonPath("$.data.contactPerson", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-03: Supplier can view performance scorecard and history")
    void testGetSupplierPerformance() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/performance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierName", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-04: Supplier can view completed evaluations")
    void testGetSupplierEvaluations() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/evaluations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-05: Supplier can view assigned improvement actions")
    void testGetSupplierImprovementActions() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/improvement-actions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-06: Supplier can view compliance documents")
    void testGetSupplierDocuments() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @WithMockUser(username = "supplier_apex", roles = {"SUPPLIER"})
    @DisplayName("PORTAL-IT-07: Supplier can view communication threads and send a message")
    void testSupplierCommunications() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/communications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));

        SupplierCommunicationCreateRequest req = new SupplierCommunicationCreateRequest(
                "Test Inquiry", "Hello from integration test", "EVALUATION", 1L);

        mockMvc.perform(post("/api/v1/supplier-portal/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.subject", is("Test Inquiry")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PORTAL-IT-08: Admin can view pending profile update requests")
    void testAdminPendingProfileRequests() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/admin/profile-requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @DisplayName("PORTAL-IT-09: Unauthenticated access to supplier portal is rejected")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/supplier-portal/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
