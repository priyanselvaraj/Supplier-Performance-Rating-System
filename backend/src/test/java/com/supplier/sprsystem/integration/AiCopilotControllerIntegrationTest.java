package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.ai.AiCopilotQueryRequest;
import com.supplier.sprsystem.dto.ai.AiFeedbackRequest;
import com.supplier.sprsystem.dto.ai.AiSupplierCompareRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AiCopilotControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("COPILOT-IT-01: ADMIN can query AI Copilot with natural language")
    void testProcessCopilotQueryAsAdmin() throws Exception {
        AiCopilotQueryRequest request = new AiCopilotQueryRequest("Which suppliers are performing poorly?", null, null);

        mockMvc.perform(post("/api/v1/ai/copilot/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.answer", notNullValue()))
                .andExpect(jsonPath("$.data.queryIntent", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("COPILOT-IT-02: MANAGER can fetch AI query interaction history")
    void testGetCopilotHistoryAsManager() throws Exception {
        mockMvc.perform(get("/api/v1/ai/copilot/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("COPILOT-IT-03: ADMIN can retrieve Executive AI decision support insights")
    void testGetExecutiveAiInsights() throws Exception {
        mockMvc.perform(get("/api/v1/ai/executive-insights")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.executiveSummary", notNullValue()))
                .andExpect(jsonPath("$.data.portfolioAverageScore", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("COPILOT-IT-04: MANAGER can compare multiple suppliers using AI")
    void testCompareSuppliers() throws Exception {
        AiSupplierCompareRequest request = new AiSupplierCompareRequest(Arrays.asList(1L, 2L));

        mockMvc.perform(post("/api/v1/ai/suppliers/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.comparativeAnalysis", notNullValue()))
                .andExpect(jsonPath("$.data.suppliers", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("COPILOT-IT-05: ADMIN can fetch workflow AI recommendations")
    void testGetWorkflowRecommendations() throws Exception {
        mockMvc.perform(get("/api/v1/ai/workflows/recommendations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
