package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ProfileUpdateRequestReviewDto;
import com.supplier.sprsystem.dto.request.SupplierAccountCreateRequest;
import com.supplier.sprsystem.dto.request.SupplierActionResponseRequest;
import com.supplier.sprsystem.dto.request.SupplierProfileUpdateSubmitRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.impl.SupplierPortalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SupplierPortalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private SupplierImprovementActionRepository improvementActionRepository;

    @Mock
    private SupplierDocumentRepository documentRepository;

    @Mock
    private SupplierProfileUpdateRequestRepository profileUpdateRequestRepository;

    @Mock
    private SupplierCommunicationRepository communicationRepository;

    @Mock
    private SupplierRatingService ratingService;

    @Mock
    private AiIntelligenceService aiIntelligenceService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SupplierPortalServiceImpl portalService;

    private User supplierUser;
    private Supplier sampleSupplier;
    private Role supplierRole;

    @BeforeEach
    void setUp() {
        supplierRole = Role.builder().id(3L).name(ERole.ROLE_SUPPLIER).build();

        sampleSupplier = Supplier.builder()
                .id(100L)
                .supplierCode("SUP-10001")
                .name("Apex Microelectronics Inc.")
                .contactPerson("Sarah Jenkins")
                .email("orders@apexmicro.com")
                .phone("+1 408-555-0144")
                .status(SupplierStatus.ACTIVE)
                .overallRating(92.5)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(4)
                .build();

        supplierUser = User.builder()
                .id(50L)
                .username("supplier_apex")
                .email("orders@apexmicro.com")
                .fullName("Sarah Jenkins")
                .active(true)
                .roles(Set.of(supplierRole))
                .supplier(sampleSupplier)
                .build();

        // Setup Security Context
        UserDetailsImpl userDetails = UserDetailsImpl.build(supplierUser);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(50L)).thenReturn(Optional.of(supplierUser));
        when(userRepository.findByUsername("supplier_apex")).thenReturn(Optional.of(supplierUser));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("PORTAL-SVC-01: Supplier can retrieve self-service dashboard successfully")
    void testGetSupplierDashboard() {
        SupplierPerformanceSummaryResponse summary = SupplierPerformanceSummaryResponse.builder()
                .supplierId(100L)
                .supplierCode("SUP-10001")
                .supplierName("Apex Microelectronics Inc.")
                .latestScore(92.5)
                .latestRating(SupplierRating.EXCELLENT)
                .latestPerformanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .scoreDifference(2.5)
                .performanceTrend(PerformanceTrend.IMPROVING)
                .ratingDate(LocalDate.now())
                .totalEvaluations(4)
                .build();

        when(ratingService.getSupplierPerformanceSummary(100L)).thenReturn(summary);
        when(improvementActionRepository.findBySupplierIdOrderByCreatedAtDesc(100L)).thenReturn(Collections.emptyList());
        when(documentRepository.countBySupplierId(100L)).thenReturn(3L);
        when(notificationService.getUnreadCount(50L)).thenReturn(1L);
        when(evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateDesc(100L, EvaluationStatus.COMPLETED))
                .thenReturn(Collections.emptyList());

        SupplierPortalDashboardResponse response = portalService.getSupplierDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getSupplierId());
        assertEquals("Apex Microelectronics Inc.", response.getSupplierName());
        assertEquals(92.5, response.getOverallRating());
        assertEquals(3L, response.getTotalDocumentsCount());
        assertEquals(1L, response.getUnreadNotificationsCount());
    }

    @Test
    @DisplayName("PORTAL-SVC-02: Supplier can view company profile details")
    void testGetSupplierProfile() {
        when(profileUpdateRequestRepository.findBySupplierIdOrderByCreatedAtDesc(100L)).thenReturn(Collections.emptyList());

        SupplierPortalProfileResponse profile = portalService.getSupplierProfile();

        assertNotNull(profile);
        assertEquals("SUP-10001", profile.getSupplierCode());
        assertEquals("Apex Microelectronics Inc.", profile.getName());
        assertFalse(profile.isHasPendingUpdateRequest());
    }

    @Test
    @DisplayName("PORTAL-SVC-03: Submitting profile update request creates pending request")
    void testSubmitProfileUpdateRequest() {
        when(profileUpdateRequestRepository.existsBySupplierIdAndStatus(100L, UpdateRequestStatus.PENDING))
                .thenReturn(false);

        SupplierProfileUpdateRequest savedReq = SupplierProfileUpdateRequest.builder()
                .id(1L)
                .supplier(sampleSupplier)
                .requestedBy(supplierUser)
                .contactPerson("Sarah Ramirez")
                .email("sarah@apex.com")
                .status(UpdateRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(profileUpdateRequestRepository.save(any(SupplierProfileUpdateRequest.class))).thenReturn(savedReq);

        SupplierProfileUpdateSubmitRequest submitReq = new SupplierProfileUpdateSubmitRequest();
        submitReq.setContactPerson("Sarah Ramirez");
        submitReq.setEmail("sarah@apex.com");

        SupplierProfileUpdateRequestDto result = portalService.submitProfileUpdateRequest(submitReq);

        assertNotNull(result);
        assertEquals(UpdateRequestStatus.PENDING, result.getStatus());
        assertEquals("Sarah Ramirez", result.getContactPerson());
        verify(notificationService).broadcastToRole(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("PORTAL-SVC-04: Cannot submit duplicate pending profile update request")
    void testDuplicateProfileUpdateRequestThrowsException() {
        when(profileUpdateRequestRepository.existsBySupplierIdAndStatus(100L, UpdateRequestStatus.PENDING))
                .thenReturn(true);

        SupplierProfileUpdateSubmitRequest submitReq = new SupplierProfileUpdateSubmitRequest();

        assertThrows(BadRequestException.class, () -> portalService.submitProfileUpdateRequest(submitReq));
    }

    @Test
    @DisplayName("PORTAL-SVC-05: Reviewing and approving profile update updates the supplier record")
    void testReviewProfileUpdateRequestApproval() {
        SupplierProfileUpdateRequest existingReq = SupplierProfileUpdateRequest.builder()
                .id(10L)
                .supplier(sampleSupplier)
                .requestedBy(supplierUser)
                .contactPerson("New Contact Name")
                .phone("+1 999-0000")
                .status(UpdateRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(profileUpdateRequestRepository.findById(10L)).thenReturn(Optional.of(existingReq));
        when(profileUpdateRequestRepository.save(any(SupplierProfileUpdateRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(i -> i.getArgument(0));

        ProfileUpdateRequestReviewDto reviewDto = new ProfileUpdateRequestReviewDto(true, "Looks good and verified.");
        SupplierProfileUpdateRequestDto result = portalService.reviewProfileUpdateRequest(10L, reviewDto);

        assertNotNull(result);
        assertEquals(UpdateRequestStatus.APPROVED, result.getStatus());
        assertEquals("New Contact Name", sampleSupplier.getContactPerson());
        assertEquals("+1 999-0000", sampleSupplier.getPhone());
        verify(supplierRepository).save(sampleSupplier);
    }

    @Test
    @DisplayName("PORTAL-SVC-06: Data isolation prevents supplier from viewing other supplier's evaluation")
    void testDataIsolationOnEvaluation() {
        Supplier otherSupplier = Supplier.builder().id(999L).name("Other Supplier").build();

        SupplierEvaluation otherEval = SupplierEvaluation.builder()
                .id(200L)
                .supplier(otherSupplier)
                .evaluationCode("EV-999")
                .status(EvaluationStatus.COMPLETED)
                .build();

        when(evaluationRepository.findById(200L)).thenReturn(Optional.of(otherEval));

        assertThrows(UnauthorizedException.class, () -> portalService.getSupplierEvaluationDetails(200L));
    }
}
