package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.KpiDefinitionRequest;
import com.supplier.sprsystem.dto.request.ReportBuilderRequest;
import com.supplier.sprsystem.dto.request.SavedReportRequest;
import com.supplier.sprsystem.model.entity.KpiCalculationType;
import com.supplier.sprsystem.model.entity.KpiCategory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class BusinessIntelligenceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-01: Admin/Manager can retrieve executive BI dashboard summary")
    void testGetBiDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/v1/bi/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalSuppliers", notNullValue()))
                .andExpect(jsonPath("$.data.topKpis", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-02: Manager can retrieve live calculated KPIs")
    void testGetCalculatedKpis() throws Exception {
        mockMvc.perform(get("/api/v1/bi/kpis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-03: Retrieve performance trends across time intervals")
    void testGetPerformanceTrends() throws Exception {
        mockMvc.perform(get("/api/v1/bi/trends")
                        .param("timeUnit", "MONTH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-04: Retrieve supplier benchmark against category and overall metrics")
    void testGetSupplierBenchmark() throws Exception {
        mockMvc.perform(get("/api/v1/bi/benchmarks")
                        .param("supplierId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierName", notNullValue()))
                .andExpect(jsonPath("$.data.categoryAverageScore", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-05: Generate interactive custom report preview")
    void testGenerateReportPreview() throws Exception {
        ReportBuilderRequest request = ReportBuilderRequest.builder()
                .reportTitle("Quarterly Electronics Review")
                .reportScope("ALL_SUPPLIERS")
                .visualization("BAR_CHART")
                .build();

        mockMvc.perform(post("/api/v1/bi/reports/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reportTitle", is("Quarterly Electronics Review")))
                .andExpect(jsonPath("$.data.tableData", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("BI-IT-06: Save and retrieve custom report configurations")
    void testSaveAndGetSavedReport() throws Exception {
        SavedReportRequest request = SavedReportRequest.builder()
                .name("Integration Test Saved Report")
                .description("Automated test configuration")
                .reportType("SUPPLIER_PERFORMANCE")
                .scope("ALL_SUPPLIERS")
                .visualization("TABLE")
                .isPublic(true)
                .build();

        mockMvc.perform(post("/api/v1/bi/reports/saved")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Integration Test Saved Report")));

        mockMvc.perform(get("/api/v1/bi/reports/saved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "MANAGER"})
    @DisplayName("BI-IT-07: Retrieve C-suite executive dashboard metrics")
    void testGetExecutiveDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/bi/executive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierOverview", notNullValue()))
                .andExpect(jsonPath("$.data.riskOverview", notNullValue()))
                .andExpect(jsonPath("$.data.strategicInsights", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("BI-IT-08: Admin can manage KPI definition configurations")
    void testAdminKpiManagement() throws Exception {
        mockMvc.perform(get("/api/v1/admin/kpis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }
}
