package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.ai.*;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.impl.AiCopilotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AiCopilotServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierImprovementActionRepository improvementActionRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private AiInteractionHistoryRepository historyRepository;

    @Mock
    private AiRecommendationDecisionRepository decisionRepository;

    @Mock
    private AiIntelligenceService aiIntelligenceService;

    @InjectMocks
    private AiCopilotServiceImpl copilotService;

    private User mockManagerUser;
    private UserDetailsImpl managerDetails;
    private Supplier mockSupplier;
    private Supplier mockSupplier2;

    @BeforeEach
    void setUp() {
        mockSupplier = Supplier.builder()
                .id(1L)
                .name("Acme Electronics")
                .supplierCode("SUP-00001")
                .overallRating(68.5)
                .ratingCategory(RatingCategory.POOR)
                .status(SupplierStatus.ACTIVE)
                .build();

        mockSupplier2 = Supplier.builder()
                .id(2L)
                .name("Apex Global")
                .supplierCode("SUP-00002")
                .overallRating(92.0)
                .ratingCategory(RatingCategory.EXCELLENT)
                .status(SupplierStatus.ACTIVE)
                .build();

        mockManagerUser = new User();
        mockManagerUser.setId(100L);
        mockManagerUser.setUsername("manager");
        mockManagerUser.setFullName("Procurement Manager");

        managerDetails = new UserDetailsImpl(
                100L,
                "manager",
                "manager@example.com",
                "Procurement Manager",
                "password",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );
    }

    @Test
    @DisplayName("Should process poor performing suppliers query and return accurate advice")
    void testProcessCopilotQuery_PoorPerformance() {
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(mockManagerUser));
        when(supplierRepository.findAll()).thenReturn(Arrays.asList(mockSupplier, mockSupplier2));

        AiInteractionHistory savedHistory = AiInteractionHistory.builder()
                .id(1L)
                .user(mockManagerUser)
                .question("Which suppliers are performing poorly?")
                .responseSummary("Identified 1 underperforming supplier...")
                .queryIntent("Identify Underperforming Suppliers")
                .intentCategory("PERFORMANCE_ANALYSIS")
                .createdAt(LocalDateTime.now())
                .build();

        when(historyRepository.save(any(AiInteractionHistory.class))).thenReturn(savedHistory);

        AiCopilotQueryRequest request = new AiCopilotQueryRequest("Which suppliers are performing poorly?", null, null);
        AiCopilotQueryResponse response = copilotService.processCopilotQuery(request, managerDetails);

        assertThat(response).isNotNull();
        assertThat(response.getQueryIntent()).isEqualTo("Identify Underperforming Suppliers");
        assertThat(response.getAnswer()).contains("Acme Electronics");
        assertThat(response.getAnswer()).contains("68.5%");
        assertThat(response.getSuggestedActions()).isNotEmpty();
    }

    @Test
    @DisplayName("Should process risk query for specific supplier")
    void testProcessCopilotQuery_RiskInquiry() {
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(mockManagerUser));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));

        SupplierRiskResponse riskResponse = SupplierRiskResponse.builder()
                .supplierId(1L)
                .supplierName("Acme Electronics")
                .supplierCode("SUP-00001")
                .currentScore(68.5)
                .ratingCategory(RatingCategory.POOR)
                .riskScore(72.0)
                .riskLevel(RiskLevel.HIGH)
                .trend(PerformanceTrend.DECLINING)
                .riskFactors(Collections.singletonList("Score below minimum threshold (70%)"))
                .explanation("High risk due to score deficit and downward trajectory.")
                .activeAlertsCount(1)
                .build();

        when(aiIntelligenceService.getSupplierRisk(1L)).thenReturn(riskResponse);

        AiInteractionHistory savedHistory = AiInteractionHistory.builder()
                .id(2L)
                .user(mockManagerUser)
                .question("Why is Acme Electronics considered high risk?")
                .responseSummary("High risk due to score deficit...")
                .createdAt(LocalDateTime.now())
                .build();

        when(historyRepository.save(any(AiInteractionHistory.class))).thenReturn(savedHistory);

        AiCopilotQueryRequest request = new AiCopilotQueryRequest("Why is Acme Electronics considered high risk?", 1L, null);
        AiCopilotQueryResponse response = copilotService.processCopilotQuery(request, managerDetails);

        assertThat(response).isNotNull();
        assertThat(response.getQueryIntent()).isEqualTo("Supplier Risk Assessment");
        assertThat(response.getAnswer()).contains("HIGH");
        assertThat(response.getAnswer()).contains("72.0 / 100");
    }

    @Test
    @DisplayName("Should submit user feedback on AI response")
    void testSubmitFeedback() {
        AiInteractionHistory history = AiInteractionHistory.builder()
                .id(5L)
                .user(mockManagerUser)
                .question("Test query")
                .responseSummary("Test response")
                .build();

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(mockManagerUser));
        when(historyRepository.findById(5L)).thenReturn(Optional.of(history));

        AiFeedbackRequest feedback = new AiFeedbackRequest(true, "Clear and actionable");
        copilotService.submitFeedback(5L, feedback, managerDetails);

        verify(historyRepository, times(1)).save(history);
        assertThat(history.getHelpful()).isTrue();
        assertThat(history.getFeedbackReason()).isEqualTo("Clear and actionable");
    }

    @Test
    @DisplayName("Should generate supplier comparison analysis")
    void testCompareSuppliers() {
        when(supplierRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(mockSupplier, mockSupplier2));

        SupplierRiskResponse risk1 = SupplierRiskResponse.builder()
                .riskLevel(RiskLevel.HIGH)
                .riskScore(72.0)
                .activeAlertsCount(1)
                .build();
        SupplierRiskResponse risk2 = SupplierRiskResponse.builder()
                .riskLevel(RiskLevel.LOW)
                .riskScore(12.0)
                .activeAlertsCount(0)
                .build();

        when(aiIntelligenceService.getSupplierRisk(1L)).thenReturn(risk1);
        when(aiIntelligenceService.getSupplierRisk(2L)).thenReturn(risk2);

        SupplierAiTrendResponse trend1 = SupplierAiTrendResponse.builder()
                .trend(PerformanceTrend.DECLINING)
                .scoreDifference(-5.0)
                .build();
        SupplierAiTrendResponse trend2 = SupplierAiTrendResponse.builder()
                .trend(PerformanceTrend.IMPROVING)
                .scoreDifference(4.0)
                .build();

        when(aiIntelligenceService.getSupplierTrend(1L)).thenReturn(trend1);
        when(aiIntelligenceService.getSupplierTrend(2L)).thenReturn(trend2);
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateAsc(anyLong(), eq(EvaluationStatus.COMPLETED)))
                .thenReturn(Collections.emptyList());

        AiSupplierCompareRequest request = new AiSupplierCompareRequest(Arrays.asList(1L, 2L));
        AiSupplierCompareResponse response = copilotService.compareSuppliers(request, managerDetails);

        assertThat(response).isNotNull();
        assertThat(response.getSuppliers()).hasSize(2);
        assertThat(response.getRecommendedSupplierId()).isEqualTo(2L);
        assertThat(response.getRecommendedSupplierName()).isEqualTo("Apex Global");
    }

    @Test
    @DisplayName("Should record human recommendation decision and create improvement action")
    void testHandleRecommendationDecision_CreateAction() {
        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(mockManagerUser));

        AiRecommendationDecision existingDecision = AiRecommendationDecision.builder()
                .id(10L)
                .supplier(mockSupplier)
                .title("Remediate Delivery Lead Times")
                .recommendationText("Delivery score is low.")
                .priority(RecommendationPriority.HIGH)
                .status(RecommendationDecisionStatus.PENDING)
                .build();

        when(decisionRepository.findById(10L)).thenReturn(Optional.of(existingDecision));
        when(decisionRepository.save(any(AiRecommendationDecision.class))).thenReturn(existingDecision);

        RecommendationDecisionRequest request = new RecommendationDecisionRequest(
                "REF-123",
                RecommendationDecisionStatus.ACTION_CREATED,
                "Approved and created CAP",
                "Expedited Freight Protocol",
                "Implement 48hr freight SLA gates",
                LocalDate.now().plusWeeks(2)
        );

        AiRecommendationDecision decision = copilotService.handleRecommendationDecision(10L, request, managerDetails);

        assertThat(decision).isNotNull();
        assertThat(decision.getStatus()).isEqualTo(RecommendationDecisionStatus.ACTION_CREATED);
        verify(improvementActionRepository, times(1)).save(any(SupplierImprovementAction.class));
    }
}
