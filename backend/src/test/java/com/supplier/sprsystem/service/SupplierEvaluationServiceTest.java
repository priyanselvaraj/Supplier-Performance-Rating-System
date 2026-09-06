package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.request.EvaluationScoreRequest;
import com.supplier.sprsystem.dto.response.EvaluationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.impl.SupplierEvaluationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierEvaluationServiceTest {

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupplierPerformanceRatingRepository performanceRatingRepository;

    @InjectMocks
    private SupplierEvaluationServiceImpl evaluationService;

    private Supplier sampleSupplier;
    private User sampleUser;
    private User otherUser;
    private EvaluationCriteria criteriaQuality;
    private EvaluationCriteria criteriaDelivery;
    private SupplierEvaluation draftEvaluation;

    @BeforeEach
    void setUp() {
        sampleSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-00001")
                .name("Apex Chipsets Ltd")
                .email("contact@apexchips.com")
                .overallRating(0.0)
                .ratingCategory(RatingCategory.UNRATED)
                .totalEvaluations(0)
                .build();

        sampleUser = User.builder()
                .id(10L)
                .username("evaluator_user")
                .fullName("Jane Evaluator")
                .build();

        otherUser = User.builder()
                .id(20L)
                .username("other_user")
                .fullName("John Other")
                .build();

        criteriaQuality = EvaluationCriteria.builder()
                .id(1L)
                .name("Product Quality")
                .code("CRIT-QUAL")
                .weight(40.0)
                .maxScore(100.0)
                .active(true)
                .build();

        criteriaDelivery = EvaluationCriteria.builder()
                .id(2L)
                .name("Delivery Performance")
                .code("CRIT-DELV")
                .weight(60.0)
                .maxScore(100.0)
                .active(true)
                .build();

        draftEvaluation = SupplierEvaluation.builder()
                .id(50L)
                .evaluationCode("EV-202608-1111")
                .supplier(sampleSupplier)
                .evaluator(sampleUser)
                .evaluationDate(LocalDate.now())
                .evaluationPeriod("Q3 2026")
                .status(EvaluationStatus.DRAFT)
                .totalWeightedScore(0.0)
                .ratingCategory(RatingCategory.UNRATED)
                .scores(new ArrayList<>())
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(
                10L, "evaluator_user", "jane@example.com", "Jane Evaluator", "pwd", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test Submit Evaluation with weighted score calculation and EXCELLENT category")
    void testSubmitEvaluation_WeightedScoreCalculation() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .evaluationDate(LocalDate.now())
                .evaluationPeriod("Q3 2026")
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(90.0).remarks("High quality").build(),
                        EvaluationScoreRequest.builder().criteriaId(2L).scoreObtained(95.0).remarks("On-time").build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(criteriaQuality));
        when(criteriaRepository.findById(2L)).thenReturn(Optional.of(criteriaDelivery));

        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenAnswer(inv -> {
            SupplierEvaluation eval = inv.getArgument(0);
            eval.setId(100L);
            return eval;
        });

        EvaluationResponse response = evaluationService.submitEvaluation(request);

        assertNotNull(response);
        assertEquals(93.0, response.getTotalWeightedScore());
        assertEquals(RatingCategory.EXCELLENT, response.getRatingCategory());
        assertEquals(EvaluationStatus.COMPLETED, response.getStatus());
        assertEquals(2, response.getScores().size());
        verify(evaluationRepository, times(1)).save(any(SupplierEvaluation.class));
        verify(supplierRepository, times(1)).save(sampleSupplier);
    }

    @Test
    @DisplayName("Test Create Draft Evaluation successfully")
    void testCreateDraftEvaluation_Success() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .draft(true)
                .generalComments("Initial draft observations")
                .scores(List.of())
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenAnswer(inv -> {
            SupplierEvaluation eval = inv.getArgument(0);
            eval.setId(101L);
            return eval;
        });

        EvaluationResponse response = evaluationService.createDraftEvaluation(request);

        assertNotNull(response);
        assertEquals(EvaluationStatus.DRAFT, response.getStatus());
        assertEquals(0.0, response.getTotalWeightedScore());
    }

    @Test
    @DisplayName("Test Submit Evaluation with Active Weights not equal to 100 throws BadRequestException")
    void testSubmitEvaluation_ActiveWeightsNot100() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(90.0).build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(85.0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> evaluationService.submitEvaluation(request));
        assertTrue(ex.getMessage().contains("must total 100%"));
    }

    @Test
    @DisplayName("Test Submit Evaluation with negative score throws BadRequestException")
    void testSubmitEvaluation_ScoreNegative() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(-5.0).build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(criteriaQuality));

        assertThrows(BadRequestException.class, () -> evaluationService.submitEvaluation(request));
    }

    @Test
    @DisplayName("Test Score exceeds maximum allowed throws BadRequestException")
    void testSubmitEvaluation_ScoreExceedsMax() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(105.0).build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(criteriaQuality));

        assertThrows(BadRequestException.class, () -> evaluationService.submitEvaluation(request));
    }

    @Test
    @DisplayName("Test Submit Evaluation with invalid supplier throws ResourceNotFoundException")
    void testSubmitEvaluation_SupplierNotFound() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(99L)
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(90.0).build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluationService.submitEvaluation(request));
    }

    @Test
    @DisplayName("Test Submit Evaluation with invalid criteria throws ResourceNotFoundException")
    void testSubmitEvaluation_CriteriaNotFound() {
        EvaluationRequest request = EvaluationRequest.builder()
                .supplierId(1L)
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(99L).scoreObtained(90.0).build()
                ))
                .build();

        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluationService.submitEvaluation(request));
    }

    @Test
    @DisplayName("Test Update Draft Evaluation successfully")
    void testUpdateDraftEvaluation_Success() {
        EvaluationRequest request = EvaluationRequest.builder()
                .generalComments("Updated comments")
                .scores(List.of(
                        EvaluationScoreRequest.builder().criteriaId(1L).scoreObtained(85.0).build(),
                        EvaluationScoreRequest.builder().criteriaId(2L).scoreObtained(90.0).build()
                ))
                .build();

        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.findById(1L)).thenReturn(Optional.of(criteriaQuality));
        when(criteriaRepository.findById(2L)).thenReturn(Optional.of(criteriaDelivery));
        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenReturn(draftEvaluation);

        EvaluationResponse response = evaluationService.updateDraftEvaluation(50L, request);

        assertNotNull(response);
        assertEquals("Updated comments", response.getGeneralComments());
        verify(evaluationRepository, times(1)).save(draftEvaluation);
    }

    @Test
    @DisplayName("Test Update Draft Evaluation when status is not DRAFT throws BadRequestException")
    void testUpdateDraftEvaluation_NonDraftStatus_ThrowsException() {
        draftEvaluation.setStatus(EvaluationStatus.COMPLETED);
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        EvaluationRequest request = EvaluationRequest.builder().generalComments("Edit completed").build();

        assertThrows(BadRequestException.class, () -> evaluationService.updateDraftEvaluation(50L, request));
    }

    @Test
    @DisplayName("Test Ownership Validation: Unauthorized user cannot edit another evaluator's draft")
    void testUpdateDraftEvaluation_UnauthorizedUser() {
        draftEvaluation.setEvaluator(otherUser);
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        EvaluationRequest request = EvaluationRequest.builder().generalComments("Attempt unauthorized edit").build();

        assertThrows(AccessDeniedException.class, () -> evaluationService.updateDraftEvaluation(50L, request));
    }

    @Test
    @DisplayName("Test Submit Draft Evaluation by ID")
    void testSubmitDraftEvaluation_Success() {
        draftEvaluation.addScore(EvaluationScore.builder()
                .criteria(criteriaQuality)
                .scoreObtained(90.0)
                .maxScore(100.0)
                .weight(40.0)
                .weightedScore(36.0)
                .build());
        draftEvaluation.addScore(EvaluationScore.builder()
                .criteria(criteriaDelivery)
                .scoreObtained(90.0)
                .maxScore(100.0)
                .weight(60.0)
                .weightedScore(54.0)
                .build());

        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(criteriaRepository.sumActiveWeights()).thenReturn(100.0);
        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenReturn(draftEvaluation);

        EvaluationResponse response = evaluationService.submitEvaluation(50L);

        assertNotNull(response);
        assertEquals(EvaluationStatus.SUBMITTED, response.getStatus());
        verify(evaluationRepository, times(1)).save(draftEvaluation);
    }

    @Test
    @DisplayName("Test Complete Evaluation")
    void testCompleteEvaluation_Success() {
        draftEvaluation.setStatus(EvaluationStatus.SUBMITTED);
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenReturn(draftEvaluation);

        EvaluationResponse response = evaluationService.completeEvaluation(50L);

        assertNotNull(response);
        assertEquals(EvaluationStatus.COMPLETED, response.getStatus());
    }

    @Test
    @DisplayName("Test Cancel Evaluation")
    void testCancelEvaluation_Success() {
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenReturn(draftEvaluation);

        EvaluationResponse response = evaluationService.cancelEvaluation(50L);

        assertNotNull(response);
        assertEquals(EvaluationStatus.CANCELLED, response.getStatus());
    }

    @Test
    @DisplayName("Test Cancel already completed evaluation throws BadRequestException")
    void testCancelEvaluation_AlreadyCompleted() {
        draftEvaluation.setStatus(EvaluationStatus.COMPLETED);
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        assertThrows(BadRequestException.class, () -> evaluationService.cancelEvaluation(50L));
    }

    @Test
    @DisplayName("Test Get My Evaluations")
    void testGetMyEvaluations_Success() {
        Page<SupplierEvaluation> page = new PageImpl<>(List.of(draftEvaluation));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(evaluationRepository.findByEvaluatorId(eq(10L), any(Pageable.class))).thenReturn(page);

        PaginatedResponse<EvaluationResponse> result = evaluationService.getMyEvaluations(0, 10, "evaluationDate", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Test Delete Evaluation")
    void testDeleteEvaluation_Success() {
        when(evaluationRepository.findById(50L)).thenReturn(Optional.of(draftEvaluation));
        doNothing().when(evaluationRepository).delete(draftEvaluation);

        assertDoesNotThrow(() -> evaluationService.deleteEvaluation(50L));
        verify(evaluationRepository, times(1)).delete(draftEvaluation);
    }
}
