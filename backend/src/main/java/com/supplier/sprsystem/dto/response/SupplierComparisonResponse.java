package com.supplier.sprsystem.dto.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierComparisonResponse {

    private List<SupplierComparisonItem> suppliers = new ArrayList<>();
    private List<String> criteriaNames = new ArrayList<>();
    private List<CriteriaComparisonRow> criteriaBreakdown = new ArrayList<>();
    private Map<String, Object> summaryStats = new HashMap<>();

    public SupplierComparisonResponse() {}

    public SupplierComparisonResponse(List<SupplierComparisonItem> suppliers, List<String> criteriaNames, List<CriteriaComparisonRow> criteriaBreakdown, Map<String, Object> summaryStats) {
        this.suppliers = suppliers != null ? suppliers : new ArrayList<>();
        this.criteriaNames = criteriaNames != null ? criteriaNames : new ArrayList<>();
        this.criteriaBreakdown = criteriaBreakdown != null ? criteriaBreakdown : new ArrayList<>();
        this.summaryStats = summaryStats != null ? summaryStats : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<SupplierComparisonItem> suppliers = new ArrayList<>();
        private List<String> criteriaNames = new ArrayList<>();
        private List<CriteriaComparisonRow> criteriaBreakdown = new ArrayList<>();
        private Map<String, Object> summaryStats = new HashMap<>();

        public Builder suppliers(List<SupplierComparisonItem> suppliers) { this.suppliers = suppliers; return this; }
        public Builder criteriaNames(List<String> criteriaNames) { this.criteriaNames = criteriaNames; return this; }
        public Builder criteriaBreakdown(List<CriteriaComparisonRow> criteriaBreakdown) { this.criteriaBreakdown = criteriaBreakdown; return this; }
        public Builder summaryStats(Map<String, Object> summaryStats) { this.summaryStats = summaryStats; return this; }

        public SupplierComparisonResponse build() {
            return new SupplierComparisonResponse(suppliers, criteriaNames, criteriaBreakdown, summaryStats);
        }
    }

    public static class SupplierComparisonItem {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String categoryName;
        private Double overallScore;
        private String ratingCategory;
        private String performanceStatus;
        private String performanceTrend;
        private Integer evaluationCount;
        private Double riskScore;
        private String riskLevel;
        private Long openImprovementActionsCount;
        private Long completedImprovementActionsCount;
        private Double slaComplianceRate;
        private Map<String, Double> criteriaScores = new HashMap<>();

        public SupplierComparisonItem() {}

        public SupplierComparisonItem(Long supplierId, String supplierCode, String supplierName, String categoryName, Double overallScore, String ratingCategory, String performanceStatus, String performanceTrend, Integer evaluationCount, Double riskScore, String riskLevel, Long openImprovementActionsCount, Long completedImprovementActionsCount, Double slaComplianceRate, Map<String, Double> criteriaScores) {
            this.supplierId = supplierId;
            this.supplierCode = supplierCode;
            this.supplierName = supplierName;
            this.categoryName = categoryName;
            this.overallScore = overallScore;
            this.ratingCategory = ratingCategory;
            this.performanceStatus = performanceStatus;
            this.performanceTrend = performanceTrend;
            this.evaluationCount = evaluationCount;
            this.riskScore = riskScore;
            this.riskLevel = riskLevel;
            this.openImprovementActionsCount = openImprovementActionsCount;
            this.completedImprovementActionsCount = completedImprovementActionsCount;
            this.slaComplianceRate = slaComplianceRate;
            this.criteriaScores = criteriaScores != null ? criteriaScores : new HashMap<>();
        }

        public static ItemBuilder builder() {
            return new ItemBuilder();
        }

        public static class ItemBuilder {
            private Long supplierId;
            private String supplierCode;
            private String supplierName;
            private String categoryName;
            private Double overallScore;
            private String ratingCategory;
            private String performanceStatus;
            private String performanceTrend;
            private Integer evaluationCount;
            private Double riskScore;
            private String riskLevel;
            private Long openImprovementActionsCount;
            private Long completedImprovementActionsCount;
            private Double slaComplianceRate;
            private Map<String, Double> criteriaScores = new HashMap<>();

            public ItemBuilder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
            public ItemBuilder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
            public ItemBuilder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
            public ItemBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
            public ItemBuilder overallScore(Double overallScore) { this.overallScore = overallScore; return this; }
            public ItemBuilder ratingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; return this; }
            public ItemBuilder performanceStatus(String performanceStatus) { this.performanceStatus = performanceStatus; return this; }
            public ItemBuilder performanceTrend(String performanceTrend) { this.performanceTrend = performanceTrend; return this; }
            public ItemBuilder evaluationCount(Integer evaluationCount) { this.evaluationCount = evaluationCount; return this; }
            public ItemBuilder riskScore(Double riskScore) { this.riskScore = riskScore; return this; }
            public ItemBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
            public ItemBuilder openImprovementActionsCount(Long openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; return this; }
            public ItemBuilder completedImprovementActionsCount(Long completedImprovementActionsCount) { this.completedImprovementActionsCount = completedImprovementActionsCount; return this; }
            public ItemBuilder slaComplianceRate(Double slaComplianceRate) { this.slaComplianceRate = slaComplianceRate; return this; }
            public ItemBuilder criteriaScores(Map<String, Double> criteriaScores) { this.criteriaScores = criteriaScores; return this; }

            public SupplierComparisonItem build() {
                return new SupplierComparisonItem(supplierId, supplierCode, supplierName, categoryName, overallScore, ratingCategory, performanceStatus, performanceTrend, evaluationCount, riskScore, riskLevel, openImprovementActionsCount, completedImprovementActionsCount, slaComplianceRate, criteriaScores);
            }
        }

        public Long getSupplierId() { return supplierId; }
        public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
        public String getSupplierCode() { return supplierCode; }
        public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
        public String getSupplierName() { return supplierName; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public Double getOverallScore() { return overallScore; }
        public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
        public String getRatingCategory() { return ratingCategory; }
        public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }
        public String getPerformanceStatus() { return performanceStatus; }
        public void setPerformanceStatus(String performanceStatus) { this.performanceStatus = performanceStatus; }
        public String getPerformanceTrend() { return performanceTrend; }
        public void setPerformanceTrend(String performanceTrend) { this.performanceTrend = performanceTrend; }
        public Integer getEvaluationCount() { return evaluationCount; }
        public void setEvaluationCount(Integer evaluationCount) { this.evaluationCount = evaluationCount; }
        public Double getRiskScore() { return riskScore; }
        public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public Long getOpenImprovementActionsCount() { return openImprovementActionsCount; }
        public void setOpenImprovementActionsCount(Long openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; }
        public Long getCompletedImprovementActionsCount() { return completedImprovementActionsCount; }
        public void setCompletedImprovementActionsCount(Long completedImprovementActionsCount) { this.completedImprovementActionsCount = completedImprovementActionsCount; }
        public Double getSlaComplianceRate() { return slaComplianceRate; }
        public void setSlaComplianceRate(Double slaComplianceRate) { this.slaComplianceRate = slaComplianceRate; }
        public Map<String, Double> getCriteriaScores() { return criteriaScores; }
        public void setCriteriaScores(Map<String, Double> criteriaScores) { this.criteriaScores = criteriaScores; }
    }

    public static class CriteriaComparisonRow {
        private String criteriaName;
        private Double weightPercentage;
        private Map<Long, Double> supplierScores = new HashMap<>();

        public CriteriaComparisonRow() {}

        public CriteriaComparisonRow(String criteriaName, Double weightPercentage, Map<Long, Double> supplierScores) {
            this.criteriaName = criteriaName;
            this.weightPercentage = weightPercentage;
            this.supplierScores = supplierScores != null ? supplierScores : new HashMap<>();
        }

        public static RowBuilder builder() {
            return new RowBuilder();
        }

        public static class RowBuilder {
            private String criteriaName;
            private Double weightPercentage;
            private Map<Long, Double> supplierScores = new HashMap<>();

            public RowBuilder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
            public RowBuilder weightPercentage(Double weightPercentage) { this.weightPercentage = weightPercentage; return this; }
            public RowBuilder supplierScores(Map<Long, Double> supplierScores) { this.supplierScores = supplierScores; return this; }

            public CriteriaComparisonRow build() {
                return new CriteriaComparisonRow(criteriaName, weightPercentage, supplierScores);
            }
        }

        public String getCriteriaName() { return criteriaName; }
        public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
        public Double getWeightPercentage() { return weightPercentage; }
        public void setWeightPercentage(Double weightPercentage) { this.weightPercentage = weightPercentage; }
        public Map<Long, Double> getSupplierScores() { return supplierScores; }
        public void setSupplierScores(Map<Long, Double> supplierScores) { this.supplierScores = supplierScores; }
    }

    public List<SupplierComparisonItem> getSuppliers() { return suppliers; }
    public void setSuppliers(List<SupplierComparisonItem> suppliers) { this.suppliers = suppliers; }
    public List<String> getCriteriaNames() { return criteriaNames; }
    public void setCriteriaNames(List<String> criteriaNames) { this.criteriaNames = criteriaNames; }
    public List<CriteriaComparisonRow> getCriteriaBreakdown() { return criteriaBreakdown; }
    public void setCriteriaBreakdown(List<CriteriaComparisonRow> criteriaBreakdown) { this.criteriaBreakdown = criteriaBreakdown; }
    public Map<String, Object> getSummaryStats() { return summaryStats; }
    public void setSummaryStats(Map<String, Object> summaryStats) { this.summaryStats = summaryStats; }
}
