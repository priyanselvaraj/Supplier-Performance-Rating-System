package com.supplier.sprsystem.integration;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AI-IT-01: ADMIN can retrieve AI analytics dashboard")
    void testGetAiDashboardAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalSuppliersAnalyzed", notNullValue()))
                .andExpect(jsonPath("$.data.riskDistribution", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("AI-IT-02: MANAGER can retrieve AI analytics dashboard")
    void testGetAiDashboardAsManager() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AI-IT-03: ADMIN can retrieve prediction for supplier")
    void testGetSupplierPrediction() throws Exception {
        mockMvc.perform(get("/api/v1/ai/suppliers/1/prediction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierId", is(1)))
                .andExpect(jsonPath("$.data.confidence", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AI-IT-04: ADMIN can retrieve risk scoring for supplier")
    void testGetSupplierRisk() throws Exception {
        mockMvc.perform(get("/api/v1/ai/suppliers/1/risk")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.riskLevel", notNullValue()))
                .andExpect(jsonPath("$.data.riskScore", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AI-IT-05: ADMIN can retrieve unified AI supplier insights")
    void testGetSupplierInsights() throws Exception {
        mockMvc.perform(get("/api/v1/ai/suppliers/1/insights")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.prediction", notNullValue()))
                .andExpect(jsonPath("$.data.risk", notNullValue()))
                .andExpect(jsonPath("$.data.trend", notNullValue()))
                .andExpect(jsonPath("$.data.recommendations", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AI-IT-06: ADMIN can retrieve all system-wide active alerts")
    void testGetAllAlerts() throws Exception {
        mockMvc.perform(get("/api/v1/ai/alerts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("AI-IT-07: Unauthenticated access to AI endpoints returns 401 Unauthorized")
    void testAiUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/ai/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
