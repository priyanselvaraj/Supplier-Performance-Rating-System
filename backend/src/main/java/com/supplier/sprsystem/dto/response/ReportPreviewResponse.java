package com.supplier.sprsystem.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportPreviewResponse {

    private String reportTitle;
    private String reportScope;
    private LocalDateTime generatedAt;
    private Integer totalRecords;

    private List<SummaryMetricCard> summaryMetrics = new ArrayList<>();
    private List<String> tableHeaders = new ArrayList<>();
    private List<Map<String, Object>> tableData = new ArrayList<>();

    private ChartDataset chartData;

    public ReportPreviewResponse() {}

    public ReportPreviewResponse(String reportTitle, String reportScope, LocalDateTime generatedAt, Integer totalRecords, List<SummaryMetricCard> summaryMetrics, List<String> tableHeaders, List<Map<String, Object>> tableData, ChartDataset chartData) {
        this.reportTitle = reportTitle;
        this.reportScope = reportScope;
        this.generatedAt = generatedAt;
        this.totalRecords = totalRecords;
        this.summaryMetrics = summaryMetrics != null ? summaryMetrics : new ArrayList<>();
        this.tableHeaders = tableHeaders != null ? tableHeaders : new ArrayList<>();
        this.tableData = tableData != null ? tableData : new ArrayList<>();
        this.chartData = chartData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reportTitle;
        private String reportScope;
        private LocalDateTime generatedAt;
        private Integer totalRecords;
        private List<SummaryMetricCard> summaryMetrics = new ArrayList<>();
        private List<String> tableHeaders = new ArrayList<>();
        private List<Map<String, Object>> tableData = new ArrayList<>();
        private ChartDataset chartData;

        public Builder reportTitle(String reportTitle) { this.reportTitle = reportTitle; return this; }
        public Builder reportScope(String reportScope) { this.reportScope = reportScope; return this; }
        public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public Builder totalRecords(Integer totalRecords) { this.totalRecords = totalRecords; return this; }
        public Builder summaryMetrics(List<SummaryMetricCard> summaryMetrics) { this.summaryMetrics = summaryMetrics; return this; }
        public Builder tableHeaders(List<String> tableHeaders) { this.tableHeaders = tableHeaders; return this; }
        public Builder tableData(List<Map<String, Object>> tableData) { this.tableData = tableData; return this; }
        public Builder chartData(ChartDataset chartData) { this.chartData = chartData; return this; }

        public ReportPreviewResponse build() {
            return new ReportPreviewResponse(reportTitle, reportScope, generatedAt, totalRecords, summaryMetrics, tableHeaders, tableData, chartData);
        }
    }

    public static class SummaryMetricCard {
        private String label;
        private String value;
        private String change;
        private String status;

        public SummaryMetricCard() {}

        public SummaryMetricCard(String label, String value, String change, String status) {
            this.label = label;
            this.value = value;
            this.change = change;
            this.status = status;
        }

        public static CardBuilder builder() {
            return new CardBuilder();
        }

        public static class CardBuilder {
            private String label;
            private String value;
            private String change;
            private String status;

            public CardBuilder label(String label) { this.label = label; return this; }
            public CardBuilder value(String value) { this.value = value; return this; }
            public CardBuilder change(String change) { this.change = change; return this; }
            public CardBuilder status(String status) { this.status = status; return this; }

            public SummaryMetricCard build() {
                return new SummaryMetricCard(label, value, change, status);
            }
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getChange() { return change; }
        public void setChange(String change) { this.change = change; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ChartDataset {
        private String chartType;
        private List<String> labels = new ArrayList<>();
        private List<ChartSeries> series = new ArrayList<>();

        public ChartDataset() {}

        public ChartDataset(String chartType, List<String> labels, List<ChartSeries> series) {
            this.chartType = chartType;
            this.labels = labels != null ? labels : new ArrayList<>();
            this.series = series != null ? series : new ArrayList<>();
        }

        public static ChartBuilder builder() {
            return new ChartBuilder();
        }

        public static class ChartBuilder {
            private String chartType;
            private List<String> labels = new ArrayList<>();
            private List<ChartSeries> series = new ArrayList<>();

            public ChartBuilder chartType(String chartType) { this.chartType = chartType; return this; }
            public ChartBuilder labels(List<String> labels) { this.labels = labels; return this; }
            public ChartBuilder series(List<ChartSeries> series) { this.series = series; return this; }

            public ChartDataset build() {
                return new ChartDataset(chartType, labels, series);
            }
        }

        public String getChartType() { return chartType; }
        public void setChartType(String chartType) { this.chartType = chartType; }
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        public List<ChartSeries> getSeries() { return series; }
        public void setSeries(List<ChartSeries> series) { this.series = series; }
    }

    public static class ChartSeries {
        private String name;
        private List<Double> data = new ArrayList<>();

        public ChartSeries() {}

        public ChartSeries(String name, List<Double> data) {
            this.name = name;
            this.data = data != null ? data : new ArrayList<>();
        }

        public static SeriesBuilder builder() {
            return new SeriesBuilder();
        }

        public static class SeriesBuilder {
            private String name;
            private List<Double> data = new ArrayList<>();

            public SeriesBuilder name(String name) { this.name = name; return this; }
            public SeriesBuilder data(List<Double> data) { this.data = data; return this; }

            public ChartSeries build() {
                return new ChartSeries(name, data);
            }
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Double> getData() { return data; }
        public void setData(List<Double> data) { this.data = data; }
    }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getReportScope() { return reportScope; }
    public void setReportScope(String reportScope) { this.reportScope = reportScope; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }
    public List<SummaryMetricCard> getSummaryMetrics() { return summaryMetrics; }
    public void setSummaryMetrics(List<SummaryMetricCard> summaryMetrics) { this.summaryMetrics = summaryMetrics; }
    public List<String> getTableHeaders() { return tableHeaders; }
    public void setTableHeaders(List<String> tableHeaders) { this.tableHeaders = tableHeaders; }
    public List<Map<String, Object>> getTableData() { return tableData; }
    public void setTableData(List<Map<String, Object>> tableData) { this.tableData = tableData; }
    public ChartDataset getChartData() { return chartData; }
    public void setChartData(ChartDataset chartData) { this.chartData = chartData; }
}
