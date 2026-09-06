package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.SupplierRating;
import com.supplier.sprsystem.service.impl.ReportExportServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportExportServiceTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportExportServiceImpl exportService;

    private SupplierPerformanceReportResponse supplierReport;
    private OverallPerformanceReportResponse overallReport;
    private SupplierEvaluationReportResponse evalReport;

    @BeforeEach
    void setUp() {
        SupplierPerformanceRatingResponse ratingHistoryItem = SupplierPerformanceRatingResponse.builder()
                .id(1L)
                .supplierId(1L)
                .score(92.5)
                .rating(SupplierRating.EXCELLENT)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .ratingDate(LocalDate.of(2026, 8, 20))
                .evaluationCode("EV-2026-001")
                .build();

        supplierReport = SupplierPerformanceReportResponse.builder()
                .supplierId(1L)
                .supplierCode("SUP-00001")
                .supplierName("Apex Chipsets Ltd")
                .categoryName("Electronics & Hardware")
                .active(true)
                .latestScore(92.5)
                .latestRating(SupplierRating.EXCELLENT)
                .performanceStatus(PerformanceStatus.HIGH_PERFORMING)
                .performanceTrend(PerformanceTrend.IMPROVING)
                .latestRatingDate(LocalDate.of(2026, 8, 20))
                .ratingHistory(List.of(ratingHistoryItem))
                .build();

        RatingDistributionResponse dist = RatingDistributionResponse.builder()
                .rating(SupplierRating.EXCELLENT)
                .count(5)
                .percentage(50.0)
                .build();

        TopSupplierResponse topSup = TopSupplierResponse.builder()
                .supplierId(1L)
                .supplierCode("SUP-00001")
                .supplierName("Apex Chipsets Ltd")
                .latestScore(92.5)
                .rating(SupplierRating.EXCELLENT)
                .build();

        overallReport = OverallPerformanceReportResponse.builder()
                .generatedAt(LocalDate.now())
                .totalSuppliers(10)
                .activeSuppliers(8)
                .inactiveSuppliers(2)
                .totalRatedSuppliers(10)
                .averagePerformanceScore(85.0)
                .highestPerformanceScore(98.0)
                .lowestPerformanceScore(62.0)
                .ratingDistribution(List.of(dist))
                .topSuppliers(List.of(topSup))
                .build();

        CriteriaScoreReportResponse critScore = CriteriaScoreReportResponse.builder()
                .criteriaName("Quality")
                .weight(40.0)
                .rawScore(95.0)
                .maxScore(100.0)
                .weightedScore(38.0)
                .comments("Exemplary QA")
                .build();

        evalReport = SupplierEvaluationReportResponse.builder()
                .evaluationId(10L)
                .evaluationCode("EV-2026-001")
                .supplierId(1L)
                .supplierCode("SUP-00001")
                .supplierName("Apex Chipsets Ltd")
                .categoryName("Electronics")
                .evaluatorName("Admin User")
                .evaluationDate(LocalDate.of(2026, 8, 20))
                .status(EvaluationStatus.COMPLETED)
                .totalScore(92.5)
                .generalComments("Great partner")
                .strengths("Fast delivery")
                .criteriaScores(List.of(critScore))
                .build();
    }

    @Test
    @DisplayName("1. Export Supplier Performance Report PDF")
    void testExportSupplierPerformanceReportPdf() {
        when(reportService.getSupplierPerformanceReport(1L, null, null)).thenReturn(supplierReport);

        byte[] pdfBytes = exportService.exportSupplierPerformanceReportPdf(1L, null, null);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // PDF magic byte check: %PDF
        assertEquals('%', (char) pdfBytes[0]);
        assertEquals('P', (char) pdfBytes[1]);
        assertEquals('D', (char) pdfBytes[2]);
        assertEquals('F', (char) pdfBytes[3]);
    }

    @Test
    @DisplayName("2. Export Supplier Performance Report Excel")
    void testExportSupplierPerformanceReportExcel() throws IOException {
        when(reportService.getSupplierPerformanceReport(1L, null, null)).thenReturn(supplierReport);

        byte[] excelBytes = exportService.exportSupplierPerformanceReportExcel(1L, null, null);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            assertNotNull(wb.getSheet("Supplier Performance"));
            assertEquals("Supplier Performance Report - Apex Chipsets Ltd",
                    wb.getSheet("Supplier Performance").getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test
    @DisplayName("3. Export Supplier Performance Report CSV")
    void testExportSupplierPerformanceReportCsv() {
        when(reportService.getSupplierPerformanceReport(1L, null, null)).thenReturn(supplierReport);

        byte[] csvBytes = exportService.exportSupplierPerformanceReportCsv(1L, null, null);

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        String csvString = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvString.contains("Supplier Performance Report"));
        assertTrue(csvString.contains("Apex Chipsets Ltd"));
        assertTrue(csvString.contains("SUP-00001"));
        assertTrue(csvString.contains("Rating Date,Score,Rating,Performance Status,Evaluation Code"));
    }

    @Test
    @DisplayName("4. Export Overall Performance Report PDF")
    void testExportOverallPerformanceReportPdf() {
        when(reportService.getOverallPerformanceReport(null, null)).thenReturn(overallReport);

        byte[] pdfBytes = exportService.exportOverallPerformanceReportPdf(null, null);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertEquals('%', (char) pdfBytes[0]);
    }

    @Test
    @DisplayName("5. Export Overall Performance Report Excel")
    void testExportOverallPerformanceReportExcel() throws IOException {
        when(reportService.getOverallPerformanceReport(null, null)).thenReturn(overallReport);

        byte[] excelBytes = exportService.exportOverallPerformanceReportExcel(null, null);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            assertNotNull(wb.getSheet("Overall Performance"));
        }
    }

    @Test
    @DisplayName("6. Export Overall Performance Report CSV")
    void testExportOverallPerformanceReportCsv() {
        when(reportService.getOverallPerformanceReport(null, null)).thenReturn(overallReport);

        byte[] csvBytes = exportService.exportOverallPerformanceReportCsv(null, null);

        assertNotNull(csvBytes);
        String csvString = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvString.contains("Overall Supplier Performance Report"));
        assertTrue(csvString.contains("Total Suppliers,10"));
        assertTrue(csvString.contains("Rating Distribution"));
    }

    @Test
    @DisplayName("7. Export Supplier Evaluation Report PDF")
    void testExportSupplierEvaluationReportPdf() {
        when(reportService.getSupplierEvaluationReport(10L)).thenReturn(evalReport);

        byte[] pdfBytes = exportService.exportSupplierEvaluationReportPdf(10L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertEquals('%', (char) pdfBytes[0]);
    }

    @Test
    @DisplayName("8. Export Supplier Evaluation Report Excel")
    void testExportSupplierEvaluationReportExcel() throws IOException {
        when(reportService.getSupplierEvaluationReport(10L)).thenReturn(evalReport);

        byte[] excelBytes = exportService.exportSupplierEvaluationReportExcel(10L);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            assertNotNull(wb.getSheet("Evaluation Scorecard"));
        }
    }

    @Test
    @DisplayName("9. Export Supplier Evaluation Report CSV")
    void testExportSupplierEvaluationReportCsv() {
        when(reportService.getSupplierEvaluationReport(10L)).thenReturn(evalReport);

        byte[] csvBytes = exportService.exportSupplierEvaluationReportCsv(10L);

        assertNotNull(csvBytes);
        String csvString = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvString.contains("Supplier Evaluation Report"));
        assertTrue(csvString.contains("EV-2026-001"));
        assertTrue(csvString.contains("Quality"));
    }
}
