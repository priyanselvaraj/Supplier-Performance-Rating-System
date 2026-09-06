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
public class SupplierImprovementActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ACTION-IT-01: Admin can retrieve improvement actions list")
    void testGetActions() throws Exception {
        mockMvc.perform(get("/api/v1/improvement-actions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("MONITOR-IT-01: Admin can retrieve monitoring summary")
    void testGetMonitoringSummary() throws Exception {
        mockMvc.perform(get("/api/v1/monitoring/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalSuppliers", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.systemHealthStatus", notNullValue()));
    }

    @Test
    @DisplayName("ACTION-IT-02: Unauthenticated access to improvement actions is blocked")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/improvement-actions"))
                .andExpect(status().isUnauthorized());
    }
}
