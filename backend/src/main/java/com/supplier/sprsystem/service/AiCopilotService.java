package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.ai.*;
import com.supplier.sprsystem.model.entity.AiRecommendationDecision;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AiCopilotService {

    AiCopilotQueryResponse processCopilotQuery(AiCopilotQueryRequest request, UserDetails currentUser);

    List<AiCopilotHistoryResponse> getInteractionHistory(UserDetails currentUser);

    void submitFeedback(Long historyId, AiFeedbackRequest feedback, UserDetails currentUser);

    AiSupplierCompareResponse compareSuppliers(AiSupplierCompareRequest request, UserDetails currentUser);

    ExecutiveAiSummaryResponse getExecutiveAiInsights(UserDetails currentUser);

    List<AiWorkflowRecommendationResponse> getWorkflowRecommendations(UserDetails currentUser);

    AiRecommendationDecision handleRecommendationDecision(Long recommendationId, RecommendationDecisionRequest request, UserDetails currentUser);

    List<AiRecommendationDecision> getRecommendationDecisions(Long supplierId, UserDetails currentUser);
}
