package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.request.EvaluationScoreRequest;
import com.supplier.sprsystem.dto.response.EvaluationResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SupplierEvaluationServiceImpl evaluationService;

    private Supplier sampleSupplier;
    private User sampleUser;
    private EvaluationCriteria criteriaQuality;
    private EvaluationCriteria criteriaDelivery;

    @BeforeEach
    void setUp() {
        sampleSupplier = Supplier.builder()
                .id(1L)
                .supplierCode("SUP-1001")
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

        criteriaQuality = EvaluationCriteria.builder()
                .id(1L)
                .name("Product Quality")
                .code("CRIT-QUAL")
                .weight(40.0)
                .maxScore(100.0)
                .build();

        criteriaDelivery = EvaluationCriteria.builder()
                .id(2L)
                .name("Delivery Performance")
                .code("CRIT-DELV")
                .weight(60.0)
                .maxScore(100.0)
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

        when(evaluationRepository.save(any(SupplierEvaluation.class))).thenAnswer(invocation -> {
            SupplierEvaluation eval = invocation.getArgument(0);
            eval.setId(100L);
            return eval;
        });

        EvaluationResponse response = evaluationService.submitEvaluation(request);

        assertNotNull(response);
        assertEquals(93.0, response.getTotalWeightedScore());
        assertEquals(RatingCategory.EXCELLENT, response.getRatingCategory());
        assertEquals(2, response.getScores().size());
        verify(evaluationRepository, times(1)).save(any(SupplierEvaluation.class));
        verify(supplierRepository, times(1)).save(sampleSupplier);
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
    @DisplayName("Test Rating Category boundaries")
    void testDetermineRatingCategory() {
        assertEquals(RatingCategory.EXCELLENT, SupplierEvaluationServiceImpl.determineRatingCategory(85.0));
        assertEquals(RatingCategory.EXCELLENT, SupplierEvaluationServiceImpl.determineRatingCategory(99.0));
        assertEquals(RatingCategory.GOOD, SupplierEvaluationServiceImpl.determineRatingCategory(70.0));
        assertEquals(RatingCategory.GOOD, SupplierEvaluationServiceImpl.determineRatingCategory(84.99));
        assertEquals(RatingCategory.AVERAGE, SupplierEvaluationServiceImpl.determineRatingCategory(50.0));
        assertEquals(RatingCategory.AVERAGE, SupplierEvaluationServiceImpl.determineRatingCategory(69.9));
        assertEquals(RatingCategory.POOR, SupplierEvaluationServiceImpl.determineRatingCategory(49.9));
        assertEquals(RatingCategory.POOR, SupplierEvaluationServiceImpl.determineRatingCategory(10.0));
    }
}
