package com.supplier.sprsystem.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.service.ReportExportService;
import com.supplier.sprsystem.service.ReportService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportServiceImpl implements ReportExportService {

    private final ReportService reportService;

    public ReportExportServiceImpl(ReportService reportService) {
        this.reportService = reportService;
    }

    // ==========================================
    // 1. SUPPLIER PERFORMANCE REPORT EXPORTS
    // ==========================================

    @Override
    public byte[] exportSupplierPerformanceReportPdf(Long supplierId, LocalDate startDate, LocalDate endDate) {
        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(supplierId, startDate, endDate);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 40, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addPdfHeader(document, "Supplier Performance Report");

            // Supplier Overview Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(15f);

            addInfoRow(infoTable, "Supplier Code", report.getSupplierCode());
            addInfoRow(infoTable, "Supplier Name", report.getSupplierName());
            addInfoRow(infoTable, "Category", report.getCategoryName());
            addInfoRow(infoTable, "Status", report.isActive() ? "ACTIVE" : "INACTIVE");
            addInfoRow(infoTable, "Latest Score", report.getLatestScore() != null ? String.valueOf(report.getLatestScore()) : "N/A");
            addInfoRow(infoTable, "Latest Rating", report.getLatestRatingDisplayName() != null ? report.getLatestRatingDisplayName() : "N/A");
            addInfoRow(infoTable, "Performance Status", report.getPerformanceStatusDisplayName() != null ? report.getPerformanceStatusDisplayName() : "N/A");
            addInfoRow(infoTable, "Performance Trend", report.getPerformanceTrendDisplayName() != null ? report.getPerformanceTrendDisplayName() : "N/A");

            document.add(infoTable);

            // History Section Header
            Paragraph historyHeader = new Paragraph("Rating & Evaluation History", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
            historyHeader.setSpacingAfter(8f);
            document.add(historyHeader);

            PdfPTable historyTable = new PdfPTable(5);
            historyTable.setWidthPercentage(100);
            historyTable.setWidths(new float[]{2f, 2.5f, 2f, 2.5f, 3f});

            addTableHeader(historyTable, "Date", "Score", "Rating", "Status", "Evaluation Code");

            if (report.getRatingHistory() == null || report.getRatingHistory().isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No rating history available for this period.", FontFactory.getFont(FontFactory.HELVETICA, 10)));
                emptyCell.setColspan(5);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(8f);
                historyTable.addCell(emptyCell);
            } else {
                for (SupplierPerformanceRatingResponse r : report.getRatingHistory()) {
                    addTableCell(historyTable, String.valueOf(r.getRatingDate()));
                    addTableCell(historyTable, String.valueOf(r.getScore()));
                    addTableCell(historyTable, r.getRatingDisplayName());
                    addTableCell(historyTable, r.getPerformanceStatusDisplayName());
                    addTableCell(historyTable, r.getEvaluationCode() != null ? r.getEvaluationCode() : "N/A");
                }
            }

            document.add(historyTable);
            addPdfFooter(document);
            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate PDF report: " + e.getMessage());
        }

        return out.toByteArray();
    }

    @Override
    public byte[] exportSupplierPerformanceReportExcel(Long supplierId, LocalDate startDate, LocalDate endDate) {
        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(supplierId, startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Supplier Performance");

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Supplier Performance Report - " + report.getSupplierName());
            titleCell.setCellStyle(titleStyle);

            // Generated date
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Generated Date: " + LocalDate.now());

            // Details
            int rowIdx = 3;
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Supplier Code", report.getSupplierCode(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Supplier Name", report.getSupplierName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Category", report.getCategoryName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Status", report.isActive() ? "ACTIVE" : "INACTIVE", dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Latest Score", String.valueOf(report.getLatestScore()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Latest Rating", report.getLatestRatingDisplayName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Performance Status", report.getPerformanceStatusDisplayName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Performance Trend", report.getPerformanceTrendDisplayName(), dataStyle);

            rowIdx++; // Blank row

            // History table
            Row historyHeaderRow = sheet.createRow(rowIdx++);
            String[] headers = {"Rating Date", "Score", "Rating", "Performance Status", "Evaluation Code"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = historyHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            if (report.getRatingHistory() != null) {
                for (SupplierPerformanceRatingResponse r : report.getRatingHistory()) {
                    Row row = sheet.createRow(rowIdx++);
                    createCell(row, 0, String.valueOf(r.getRatingDate()), dataStyle);
                    createCell(row, 1, r.getScore() != null ? String.valueOf(r.getScore()) : "", dataStyle);
                    createCell(row, 2, r.getRatingDisplayName(), dataStyle);
                    createCell(row, 3, r.getPerformanceStatusDisplayName(), dataStyle);
                    createCell(row, 4, r.getEvaluationCode() != null ? r.getEvaluationCode() : "N/A", dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BadRequestException("Failed to generate Excel report: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportSupplierPerformanceReportCsv(Long supplierId, LocalDate startDate, LocalDate endDate) {
        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(supplierId, startDate, endDate);
        StringBuilder csv = new StringBuilder();

        csv.append("Supplier Performance Report\n");
        csv.append("Generated Date,").append(escapeCsv(LocalDate.now())).append("\n\n");

        csv.append("Supplier Code,").append(escapeCsv(report.getSupplierCode())).append("\n");
        csv.append("Supplier Name,").append(escapeCsv(report.getSupplierName())).append("\n");
        csv.append("Category,").append(escapeCsv(report.getCategoryName())).append("\n");
        csv.append("Status,").append(report.isActive() ? "ACTIVE" : "INACTIVE").append("\n");
        csv.append("Latest Score,").append(report.getLatestScore() != null ? report.getLatestScore() : "").append("\n");
        csv.append("Latest Rating,").append(escapeCsv(report.getLatestRatingDisplayName())).append("\n");
        csv.append("Performance Status,").append(escapeCsv(report.getPerformanceStatusDisplayName())).append("\n");
        csv.append("Performance Trend,").append(escapeCsv(report.getPerformanceTrendDisplayName())).append("\n\n");

        csv.append("Rating Date,Score,Rating,Performance Status,Evaluation Code\n");
        if (report.getRatingHistory() != null) {
            for (SupplierPerformanceRatingResponse r : report.getRatingHistory()) {
                csv.append(escapeCsv(r.getRatingDate())).append(",")
                        .append(r.getScore() != null ? r.getScore() : "").append(",")
                        .append(escapeCsv(r.getRatingDisplayName())).append(",")
                        .append(escapeCsv(r.getPerformanceStatusDisplayName())).append(",")
                        .append(escapeCsv(r.getEvaluationCode() != null ? r.getEvaluationCode() : "N/A")).append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ==========================================
    // 2. OVERALL PERFORMANCE REPORT EXPORTS
    // ==========================================

    @Override
    public byte[] exportOverallPerformanceReportPdf(LocalDate startDate, LocalDate endDate) {
        OverallPerformanceReportResponse report = reportService.getOverallPerformanceReport(startDate, endDate);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 40, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addPdfHeader(document, "Overall Supplier Performance Report");

            // Overview Summary Metrics
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10f);
            summaryTable.setSpacingAfter(15f);

            addInfoRow(summaryTable, "Total Suppliers", String.valueOf(report.getTotalSuppliers()));
            addInfoRow(summaryTable, "Active Suppliers", String.valueOf(report.getActiveSuppliers()));
            addInfoRow(summaryTable, "Inactive Suppliers", String.valueOf(report.getInactiveSuppliers()));
            addInfoRow(summaryTable, "Total Rated Suppliers", String.valueOf(report.getTotalRatedSuppliers()));
            addInfoRow(summaryTable, "Average Performance Score", String.valueOf(report.getAveragePerformanceScore()));
            addInfoRow(summaryTable, "Highest Score", String.valueOf(report.getHighestPerformanceScore()));
            addInfoRow(summaryTable, "Lowest Score", String.valueOf(report.getLowestPerformanceScore()));

            document.add(summaryTable);

            // Rating Distribution Table
            Paragraph distHeader = new Paragraph("Rating Distribution", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
            distHeader.setSpacingAfter(6f);
            document.add(distHeader);

            PdfPTable distTable = new PdfPTable(3);
            distTable.setWidthPercentage(100);
            distTable.setSpacingAfter(15f);
            addTableHeader(distTable, "Rating Level", "Count", "Percentage");

            if (report.getRatingDistribution() != null) {
                for (RatingDistributionResponse d : report.getRatingDistribution()) {
                    addTableCell(distTable, d.getRatingDisplayName());
                    addTableCell(distTable, String.valueOf(d.getCount()));
                    addTableCell(distTable, d.getPercentage() + "%");
                }
            }
            document.add(distTable);

            // Top Suppliers Table
            Paragraph topHeader = new Paragraph("Top Performing Suppliers", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
            topHeader.setSpacingAfter(6f);
            document.add(topHeader);

            PdfPTable topTable = new PdfPTable(4);
            topTable.setWidthPercentage(100);
            addTableHeader(topTable, "Code", "Supplier Name", "Score", "Rating");

            if (report.getTopSuppliers() != null) {
                for (TopSupplierResponse s : report.getTopSuppliers()) {
                    addTableCell(topTable, s.getSupplierCode());
                    addTableCell(topTable, s.getSupplierName());
                    addTableCell(topTable, String.valueOf(s.getLatestScore()));
                    addTableCell(topTable, s.getRatingDisplayName());
                }
            }
            document.add(topTable);

            addPdfFooter(document);
            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate PDF report: " + e.getMessage());
        }

        return out.toByteArray();
    }

    @Override
    public byte[] exportOverallPerformanceReportExcel(LocalDate startDate, LocalDate endDate) {
        OverallPerformanceReportResponse report = reportService.getOverallPerformanceReport(startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Overall Performance");

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Overall Supplier Performance Report");
            titleCell.setCellStyle(titleStyle);

            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Generated Date: " + LocalDate.now());

            int rowIdx = 3;
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Total Suppliers", String.valueOf(report.getTotalSuppliers()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Active Suppliers", String.valueOf(report.getActiveSuppliers()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Inactive Suppliers", String.valueOf(report.getInactiveSuppliers()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Total Rated Suppliers", String.valueOf(report.getTotalRatedSuppliers()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Average Performance Score", String.valueOf(report.getAveragePerformanceScore()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Highest Score", String.valueOf(report.getHighestPerformanceScore()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Lowest Score", String.valueOf(report.getLowestPerformanceScore()), dataStyle);

            rowIdx++; // Blank row

            // Rating Distribution Table
            Row distHeaderRow = sheet.createRow(rowIdx++);
            createCell(distHeaderRow, 0, "Rating Level", headerStyle);
            createCell(distHeaderRow, 1, "Count", headerStyle);
            createCell(distHeaderRow, 2, "Percentage", headerStyle);

            if (report.getRatingDistribution() != null) {
                for (RatingDistributionResponse d : report.getRatingDistribution()) {
                    Row row = sheet.createRow(rowIdx++);
                    createCell(row, 0, d.getRatingDisplayName(), dataStyle);
                    createCell(row, 1, String.valueOf(d.getCount()), dataStyle);
                    createCell(row, 2, d.getPercentage() + "%", dataStyle);
                }
            }

            rowIdx++; // Blank row

            // Top Suppliers Table
            Row topHeaderRow = sheet.createRow(rowIdx++);
            createCell(topHeaderRow, 0, "Supplier Code", headerStyle);
            createCell(topHeaderRow, 1, "Supplier Name", headerStyle);
            createCell(topHeaderRow, 2, "Latest Score", headerStyle);
            createCell(topHeaderRow, 3, "Rating", headerStyle);

            if (report.getTopSuppliers() != null) {
                for (TopSupplierResponse s : report.getTopSuppliers()) {
                    Row row = sheet.createRow(rowIdx++);
                    createCell(row, 0, s.getSupplierCode(), dataStyle);
                    createCell(row, 1, s.getSupplierName(), dataStyle);
                    createCell(row, 2, String.valueOf(s.getLatestScore()), dataStyle);
                    createCell(row, 3, s.getRatingDisplayName(), dataStyle);
                }
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BadRequestException("Failed to generate Excel report: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportOverallPerformanceReportCsv(LocalDate startDate, LocalDate endDate) {
        OverallPerformanceReportResponse report = reportService.getOverallPerformanceReport(startDate, endDate);
        StringBuilder csv = new StringBuilder();

        csv.append("Overall Supplier Performance Report\n");
        csv.append("Generated Date,").append(escapeCsv(LocalDate.now())).append("\n\n");

        csv.append("Total Suppliers,").append(report.getTotalSuppliers()).append("\n");
        csv.append("Active Suppliers,").append(report.getActiveSuppliers()).append("\n");
        csv.append("Inactive Suppliers,").append(report.getInactiveSuppliers()).append("\n");
        csv.append("Total Rated Suppliers,").append(report.getTotalRatedSuppliers()).append("\n");
        csv.append("Average Performance Score,").append(report.getAveragePerformanceScore()).append("\n");
        csv.append("Highest Score,").append(report.getHighestPerformanceScore()).append("\n");
        csv.append("Lowest Score,").append(report.getLowestPerformanceScore()).append("\n\n");

        csv.append("Rating Distribution\n");
        csv.append("Rating Level,Count,Percentage\n");
        if (report.getRatingDistribution() != null) {
            for (RatingDistributionResponse d : report.getRatingDistribution()) {
                csv.append(escapeCsv(d.getRatingDisplayName())).append(",")
                        .append(d.getCount()).append(",")
                        .append(d.getPercentage()).append("%\n");
            }
        }

        csv.append("\nTop Performing Suppliers\n");
        csv.append("Supplier Code,Supplier Name,Score,Rating\n");
        if (report.getTopSuppliers() != null) {
            for (TopSupplierResponse s : report.getTopSuppliers()) {
                csv.append(escapeCsv(s.getSupplierCode())).append(",")
                        .append(escapeCsv(s.getSupplierName())).append(",")
                        .append(s.getLatestScore()).append(",")
                        .append(escapeCsv(s.getRatingDisplayName())).append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ==========================================
    // 3. SUPPLIER EVALUATION REPORT EXPORTS
    // ==========================================

    @Override
    public byte[] exportSupplierEvaluationReportPdf(Long evaluationId) {
        SupplierEvaluationReportResponse report = reportService.getSupplierEvaluationReport(evaluationId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 40, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addPdfHeader(document, "Supplier Evaluation Scorecard Report");

            // Evaluation Meta Information Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(15f);

            addInfoRow(infoTable, "Evaluation Code", report.getEvaluationCode());
            addInfoRow(infoTable, "Supplier Name", report.getSupplierName());
            addInfoRow(infoTable, "Supplier Code", report.getSupplierCode());
            addInfoRow(infoTable, "Category", report.getCategoryName());
            addInfoRow(infoTable, "Evaluator", report.getEvaluatorName());
            addInfoRow(infoTable, "Evaluation Date", String.valueOf(report.getEvaluationDate()));
            addInfoRow(infoTable, "Status", String.valueOf(report.getStatus()));
            addInfoRow(infoTable, "Total Score", String.valueOf(report.getTotalScore()));

            document.add(infoTable);

            // Criteria Scores Table
            Paragraph criteriaHeader = new Paragraph("Detailed Criteria Breakdown", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
            criteriaHeader.setSpacingAfter(6f);
            document.add(criteriaHeader);

            PdfPTable scoreTable = new PdfPTable(5);
            scoreTable.setWidthPercentage(100);
            scoreTable.setWidths(new float[]{3.5f, 1.5f, 1.5f, 1.5f, 3f});

            addTableHeader(scoreTable, "Criteria Name", "Weight %", "Score", "Weighted", "Remarks");

            if (report.getCriteriaScores() != null) {
                for (CriteriaScoreReportResponse c : report.getCriteriaScores()) {
                    addTableCell(scoreTable, c.getCriteriaName());
                    addTableCell(scoreTable, String.valueOf(c.getWeight()));
                    addTableCell(scoreTable, c.getRawScore() + " / " + c.getMaxScore());
                    addTableCell(scoreTable, String.valueOf(c.getWeightedScore()));
                    addTableCell(scoreTable, c.getComments() != null ? c.getComments() : "-");
                }
            }
            document.add(scoreTable);

            // Qualitative Feedback
            if (report.getGeneralComments() != null || report.getStrengths() != null || report.getAreasForImprovement() != null) {
                Paragraph feedbackHeader = new Paragraph("Qualitative Feedback & Recommendations", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
                feedbackHeader.setSpacingBefore(10f);
                feedbackHeader.setSpacingAfter(6f);
                document.add(feedbackHeader);

                PdfPTable feedbackTable = new PdfPTable(2);
                feedbackTable.setWidthPercentage(100);
                if (report.getStrengths() != null) addInfoRow(feedbackTable, "Key Strengths", report.getStrengths());
                if (report.getAreasForImprovement() != null) addInfoRow(feedbackTable, "Areas for Improvement", report.getAreasForImprovement());
                if (report.getRecommendation() != null) addInfoRow(feedbackTable, "Recommendation", report.getRecommendation());
                if (report.getGeneralComments() != null) addInfoRow(feedbackTable, "General Comments", report.getGeneralComments());
                document.add(feedbackTable);
            }

            addPdfFooter(document);
            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate PDF report: " + e.getMessage());
        }

        return out.toByteArray();
    }

    @Override
    public byte[] exportSupplierEvaluationReportExcel(Long evaluationId) {
        SupplierEvaluationReportResponse report = reportService.getSupplierEvaluationReport(evaluationId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Evaluation Scorecard");

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Supplier Evaluation Report - " + report.getEvaluationCode());
            titleCell.setCellStyle(titleStyle);

            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Generated Date: " + LocalDate.now());

            int rowIdx = 3;
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Evaluation Code", report.getEvaluationCode(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Supplier Name", report.getSupplierName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Supplier Code", report.getSupplierCode(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Category", report.getCategoryName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Evaluator", report.getEvaluatorName(), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Evaluation Date", String.valueOf(report.getEvaluationDate()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Status", String.valueOf(report.getStatus()), dataStyle);
            rowIdx = createExcelLabelValueRow(sheet, rowIdx, "Total Score", String.valueOf(report.getTotalScore()), dataStyle);

            rowIdx++; // Blank row

            // Criteria Table
            Row criteriaHeaderRow = sheet.createRow(rowIdx++);
            String[] headers = {"Criteria Name", "Weight", "Score Obtained", "Max Score", "Weighted Score", "Remarks"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = criteriaHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            if (report.getCriteriaScores() != null) {
                for (CriteriaScoreReportResponse c : report.getCriteriaScores()) {
                    Row row = sheet.createRow(rowIdx++);
                    createCell(row, 0, c.getCriteriaName(), dataStyle);
                    createCell(row, 1, String.valueOf(c.getWeight()), dataStyle);
                    createCell(row, 2, String.valueOf(c.getRawScore()), dataStyle);
                    createCell(row, 3, String.valueOf(c.getMaxScore()), dataStyle);
                    createCell(row, 4, String.valueOf(c.getWeightedScore()), dataStyle);
                    createCell(row, 5, c.getComments() != null ? c.getComments() : "-", dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BadRequestException("Failed to generate Excel report: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportSupplierEvaluationReportCsv(Long evaluationId) {
        SupplierEvaluationReportResponse report = reportService.getSupplierEvaluationReport(evaluationId);
        StringBuilder csv = new StringBuilder();

        csv.append("Supplier Evaluation Report\n");
        csv.append("Generated Date,").append(escapeCsv(LocalDate.now())).append("\n\n");

        csv.append("Evaluation Code,").append(escapeCsv(report.getEvaluationCode())).append("\n");
        csv.append("Supplier Name,").append(escapeCsv(report.getSupplierName())).append("\n");
        csv.append("Supplier Code,").append(escapeCsv(report.getSupplierCode())).append("\n");
        csv.append("Category,").append(escapeCsv(report.getCategoryName())).append("\n");
        csv.append("Evaluator,").append(escapeCsv(report.getEvaluatorName())).append("\n");
        csv.append("Evaluation Date,").append(escapeCsv(report.getEvaluationDate())).append("\n");
        csv.append("Status,").append(escapeCsv(report.getStatus())).append("\n");
        csv.append("Total Score,").append(report.getTotalScore()).append("\n\n");

        csv.append("Criteria Name,Weight,Score Obtained,Max Score,Weighted Score,Remarks\n");
        if (report.getCriteriaScores() != null) {
            for (CriteriaScoreReportResponse c : report.getCriteriaScores()) {
                csv.append(escapeCsv(c.getCriteriaName())).append(",")
                        .append(c.getWeight()).append(",")
                        .append(c.getRawScore()).append(",")
                        .append(c.getMaxScore()).append(",")
                        .append(c.getWeightedScore()).append(",")
                        .append(escapeCsv(c.getComments() != null ? c.getComments() : "-")).append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ==========================================
    // HELPER METHODS (PDF, EXCEL, CSV)
    // ==========================================

    private void addPdfHeader(Document document, String reportTitle) throws DocumentException {
        Paragraph mainTitle = new Paragraph("SUPPLIER PERFORMANCE RATING SYSTEM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(30, 64, 175)));
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTitle);

        Paragraph subTitle = new Paragraph(reportTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.DARK_GRAY));
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(4f);
        document.add(subTitle);

        Paragraph genDate = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE), FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
        genDate.setAlignment(Element.ALIGN_CENTER);
        genDate.setSpacingAfter(12f);
        document.add(genDate);
    }

    private void addPdfFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("\nGenerated by Supplier Performance Rating System (SPRS) • Confidential & Proprietary",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(15f);
        document.add(footer);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(59, 130, 246));
            cell.setPadding(6f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", cellFont));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new Color(243, 244, 246));
        labelCell.setPadding(5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        valueCell.setPadding(5f);
        table.addCell(valueCell);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private int createExcelLabelValueRow(Sheet sheet, int rowIdx, String label, String value, CellStyle dataStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(dataStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value : "N/A");
        valueCell.setCellStyle(dataStyle);
        return rowIdx + 1;
    }

    private void createCell(Row row, int colIdx, String value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private String escapeCsv(Object val) {
        if (val == null) return "";
        String s = String.valueOf(val);
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
