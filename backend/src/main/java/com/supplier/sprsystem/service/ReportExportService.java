package com.supplier.sprsystem.service;

import java.time.LocalDate;

public interface ReportExportService {

    byte[] exportSupplierPerformanceReportPdf(Long supplierId, LocalDate startDate, LocalDate endDate);

    byte[] exportSupplierPerformanceReportExcel(Long supplierId, LocalDate startDate, LocalDate endDate);

    byte[] exportSupplierPerformanceReportCsv(Long supplierId, LocalDate startDate, LocalDate endDate);

    byte[] exportOverallPerformanceReportPdf(LocalDate startDate, LocalDate endDate);

    byte[] exportOverallPerformanceReportExcel(LocalDate startDate, LocalDate endDate);

    byte[] exportOverallPerformanceReportCsv(LocalDate startDate, LocalDate endDate);

    byte[] exportSupplierEvaluationReportPdf(Long evaluationId);

    byte[] exportSupplierEvaluationReportExcel(Long evaluationId);

    byte[] exportSupplierEvaluationReportCsv(Long evaluationId);
}
