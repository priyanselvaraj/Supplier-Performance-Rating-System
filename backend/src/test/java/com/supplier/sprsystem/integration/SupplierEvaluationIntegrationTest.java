package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.request.EvaluationScoreRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SupplierEvaluationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("IT-20: Fetch all evaluations list as ADMIN")
    void testGetEvaluationsList() throws Exception {
        mockMvc.perform(get("/api/v1/evaluations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("IT-21: Save draft evaluation scorecard as ADMIN")
    void testSaveDraftEvaluation() throws Exception {
        EvaluationScoreRequest scoreReq = EvaluationScoreRequest.builder()
                .criteriaId(1L)
                .scoreObtained(90.0)
                .remarks("Draft QA Score")
                .build();

        EvaluationRequest req = EvaluationRequest.builder()
                .supplierId(1L)
                .evaluationDate(LocalDate.now())
                .evaluationPeriod("Q3 2026")
                .generalComments("Draft evaluation review")
                .scores(List.of(scoreReq))
                .build();

        mockMvc.perform(post("/api/v1/evaluations/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.evaluationCode", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("DRAFT")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("IT-22: Submit complete evaluation scorecard with all criteria")
    void testSubmitEvaluationSuccess() throws Exception {
        List<EvaluationScoreRequest> allScores = List.of(
                EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(92.0).remarks("Product Quality").build(),
                EvaluationScoreRequest.builder().criteriaId(2L).scoreObtained(88.0).remarks("Delivery").build(),
                EvaluationScoreRequest.builder().criteriaId(3L).scoreObtained(85.0).remarks("Pricing").build(),
                EvaluationScoreRequest.builder().criteriaId(4L).scoreObtained(90.0).remarks("Service").build()
        );

        EvaluationRequest req = EvaluationRequest.builder()
                .supplierId(1L)
                .evaluationDate(LocalDate.now())
                .evaluationPeriod("Q3 2026")
                .generalComments("Complete audited evaluation")
                .scores(allScores)
                .build();

        mockMvc.perform(post("/api/v1/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.evaluationCode", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }
}
