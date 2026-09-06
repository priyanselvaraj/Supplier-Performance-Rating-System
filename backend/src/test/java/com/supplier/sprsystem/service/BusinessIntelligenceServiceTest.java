package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ReportBuilderRequest;
import com.supplier.sprsystem.dto.request.SavedReportRequest;
import com.supplier.sprsystem.dto.request.SupplierComparisonRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.impl.BusinessIntelligenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BusinessIntelligenceServiceTest {

    @Mock
    private KpiService kpiService;

    @Mock
    private DashboardAnalyticsService dashboardAnalyticsService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierCategoryRepository categoryRepository;

    @Mock
    private SupplierEvaluationRepository evaluationRepository;

    @Mock
    private EvaluationCriteriaRepository criteriaRepository;

    @Mock
    private SupplierImprovementActionRepository improvementActionRepository;

    @Mock
    private ApprovalTaskRepository approvalTaskRepository;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private SavedReportRepository savedReportRepository;

    @InjectMocks
    private BusinessIntelligenceServiceImpl biService;

    private User adminUser;
    private User regularUser;
    private Supplier supplier1;
    private Supplier supplier2;
    private SupplierCategory electronicsCat;

    @BeforeEach
    void setUp() {
        electronicsCat = SupplierCategory.builder()
                .id(1L)
                .name("Electronics")
                .code("ELEC")
                .description("Electronic parts")
                .active(true)
                .build();

        Role adminRole = new Role(1L, ERole.ROLE_ADMIN);
        Role managerRole = new Role(2L, ERole.ROLE_MANAGER);

        adminUser = User.builder().id(1L).username("admin").roles(Set.of(adminRole)).build();
        regularUser = User.builder().id(2L).username("manager").roles(Set.of(managerRole)).build();

        supplier1 = Supplier.builder()
                .id(101L)
                .supplierCode("SUP-101")
                .name("Apex Micro")
                .category(electronicsCat)
                .status(SupplierStatus.ACTIVE)
                .overallRating(92.5)
                .ratingCategory(RatingCategory.EXCELLENT)
                .totalEvaluations(3)
                .build();

        supplier2 = Supplier.builder()
                .id(102L)
                .supplierCode("SUP-102")
                .name("Nexus Circuits")
                .category(electronicsCat)
                .status(SupplierStatus.ACTIVE)
                .overallRating(78.0)
                .ratingCategory(RatingCategory.GOOD)
                .totalEvaluations(2)
                .build();
    }

    @Test
    @DisplayName("Should retrieve BI dashboard summary")
    void shouldGetBiDashboardSummary() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));
        when(evaluationRepository.count()).thenReturn(5L);
        when(kpiService.calculateAllActiveKpis()).thenReturn(Collections.emptyList());

        BiDashboardResponse response = biService.getBiDashboardSummary();

        assertNotNull(response);
        assertEquals(2L, response.getTotalSuppliers());
        assertEquals(2L, response.getActiveSuppliers());
        assertEquals(85.25, response.getAverageSupplierScore());
    }

    @Test
    @DisplayName("Should compare suppliers side-by-side")
    void shouldCompareSuppliers() {
        SupplierComparisonRequest req = SupplierComparisonRequest.builder()
                .supplierIds(List.of(101L, 102L))
                .build();

        when(supplierRepository.findAllById(List.of(101L, 102L))).thenReturn(List.of(supplier1, supplier2));

        EvaluationCriteria c1 = EvaluationCriteria.builder().id(1L).name("Quality").weight(40.0).maxScore(100.0).active(true).build();
        EvaluationCriteria c2 = EvaluationCriteria.builder().id(2L).name("Delivery").weight(30.0).maxScore(100.0).active(true).build();
        when(criteriaRepository.findByActiveTrueOrderByDisplayOrderAsc()).thenReturn(List.of(c1, c2));

        SupplierComparisonResponse response = biService.compareSuppliers(req);

        assertNotNull(response);
        assertEquals(2, response.getSuppliers().size());
        assertEquals("Apex Micro", response.getSuppliers().get(0).getSupplierName());
        assertEquals(92.5, response.getSummaryStats().get("highestScore"));
    }

    @Test
    @DisplayName("Should reject comparison if fewer than 2 supplier IDs provided")
    void shouldRejectComparisonWithInsufficientSuppliers() {
        SupplierComparisonRequest req = SupplierComparisonRequest.builder()
                .supplierIds(List.of(101L))
                .build();

        assertThrows(BadRequestException.class, () -> biService.compareSuppliers(req));
    }

    @Test
    @DisplayName("Should calculate supplier benchmark metrics")
    void shouldGetSupplierBenchmark() {
        when(supplierRepository.findById(101L)).thenReturn(Optional.of(supplier1));
        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));

        EvaluationCriteria c1 = EvaluationCriteria.builder().id(1L).name("Quality").maxScore(100.0).active(true).build();
        when(criteriaRepository.findByActiveTrueOrderByDisplayOrderAsc()).thenReturn(List.of(c1));

        BenchmarkResponse response = biService.getSupplierBenchmark(101L);

        assertNotNull(response);
        assertEquals("Apex Micro", response.getSupplierName());
        assertEquals(92.5, response.getSupplierScore());
        assertEquals(85.25, response.getCategoryAverageScore());
        assertEquals(7.25, response.getCategoryDelta()); // 92.5 - 85.25 = +7.25 pts
    }

    @Test
    @DisplayName("Should generate report preview")
    void shouldGenerateReportPreview() {
        ReportBuilderRequest request = ReportBuilderRequest.builder()
                .reportTitle("Custom Electronics Review")
                .reportScope("CATEGORY")
                .categoryId(1L)
                .build();

        when(supplierRepository.findAll()).thenReturn(List.of(supplier1, supplier2));

        ReportPreviewResponse response = biService.generateReportPreview(request);

        assertNotNull(response);
        assertEquals("Custom Electronics Review", response.getReportTitle());
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getTableData().size());
    }

    @Test
    @DisplayName("Should enforce user isolation when accessing private saved report")
    void shouldEnforceUserIsolationOnPrivateReport() {
        SavedReport privateReport = SavedReport.builder()
                .id(10L)
                .name("Private Audit")
                .reportType("RISK")
                .isPublic(false)
                .createdBy(adminUser) // Owned by admin
                .build();

        when(savedReportRepository.findById(10L)).thenReturn(Optional.of(privateReport));

        // Attempt to access by regularUser who is not the creator or admin
        assertThrows(AccessDeniedException.class, () -> biService.getSavedReportById(10L, regularUser));
    }

    @Test
    @DisplayName("Should allow admin or creator to delete saved report")
    void shouldAllowAdminToDeleteSavedReport() {
        SavedReport report = SavedReport.builder()
                .id(10L)
                .name("Report To Delete")
                .isPublic(false)
                .createdBy(regularUser)
                .build();

        when(savedReportRepository.findById(10L)).thenReturn(Optional.of(report));

        biService.deleteSavedReport(10L, adminUser);

        verify(savedReportRepository, times(1)).delete(report);
    }
}
