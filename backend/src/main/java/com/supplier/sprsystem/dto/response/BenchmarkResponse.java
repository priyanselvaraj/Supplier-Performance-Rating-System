package com.supplier.sprsystem.dto.response;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Long categoryId;
    private String categoryName;

    private Double supplierScore;
    private Double categoryAverageScore;
    private Double overallAverageScore;

    private Double categoryDelta;
    private Double overallDelta;

    private Double percentileRankInCategory;
    private Double percentileRankOverall;

    private Integer totalSuppliersInCategory;
    private Integer totalSuppliersOverall;

    private List<CriteriaBenchmarkItem> criteriaBenchmarks = new ArrayList<>();
    private List<String> strengthAreas = new ArrayList<>();
    private List<String> gapAreas = new ArrayList<>();

    public BenchmarkResponse() {}

    public BenchmarkResponse(Long supplierId, String supplierCode, String supplierName, Long categoryId, String categoryName, Double supplierScore, Double categoryAverageScore, Double overallAverageScore, Double categoryDelta, Double overallDelta, Double percentileRankInCategory, Double percentileRankOverall, Integer totalSuppliersInCategory, Integer totalSuppliersOverall, List<CriteriaBenchmarkItem> criteriaBenchmarks, List<String> strengthAreas, List<String> gapAreas) {
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.supplierScore = supplierScore;
        this.categoryAverageScore = categoryAverageScore;
        this.overallAverageScore = overallAverageScore;
        this.categoryDelta = categoryDelta;
        this.overallDelta = overallDelta;
        this.percentileRankInCategory = percentileRankInCategory;
        this.percentileRankOverall = percentileRankOverall;
        this.totalSuppliersInCategory = totalSuppliersInCategory;
        this.totalSuppliersOverall = totalSuppliersOverall;
        this.criteriaBenchmarks = criteriaBenchmarks != null ? criteriaBenchmarks : new ArrayList<>();
        this.strengthAreas = strengthAreas != null ? strengthAreas : new ArrayList<>();
        this.gapAreas = gapAreas != null ? gapAreas : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private Long categoryId;
        private String categoryName;
        private Double supplierScore;
        private Double categoryAverageScore;
        private Double overallAverageScore;
        private Double categoryDelta;
        private Double overallDelta;
        private Double percentileRankInCategory;
        private Double percentileRankOverall;
        private Integer totalSuppliersInCategory;
        private Integer totalSuppliersOverall;
        private List<CriteriaBenchmarkItem> criteriaBenchmarks = new ArrayList<>();
        private List<String> strengthAreas = new ArrayList<>();
        private List<String> gapAreas = new ArrayList<>();

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder supplierScore(Double supplierScore) { this.supplierScore = supplierScore; return this; }
        public Builder categoryAverageScore(Double categoryAverageScore) { this.categoryAverageScore = categoryAverageScore; return this; }
        public Builder overallAverageScore(Double overallAverageScore) { this.overallAverageScore = overallAverageScore; return this; }
        public Builder categoryDelta(Double categoryDelta) { this.categoryDelta = categoryDelta; return this; }
        public Builder overallDelta(Double overallDelta) { this.overallDelta = overallDelta; return this; }
        public Builder percentileRankInCategory(Double percentileRankInCategory) { this.percentileRankInCategory = percentileRankInCategory; return this; }
        public Builder percentileRankOverall(Double percentileRankOverall) { this.percentileRankOverall = percentileRankOverall; return this; }
        public Builder totalSuppliersInCategory(Integer totalSuppliersInCategory) { this.totalSuppliersInCategory = totalSuppliersInCategory; return this; }
        public Builder totalSuppliersOverall(Integer totalSuppliersOverall) { this.totalSuppliersOverall = totalSuppliersOverall; return this; }
        public Builder criteriaBenchmarks(List<CriteriaBenchmarkItem> criteriaBenchmarks) { this.criteriaBenchmarks = criteriaBenchmarks; return this; }
        public Builder strengthAreas(List<String> strengthAreas) { this.strengthAreas = strengthAreas; return this; }
        public Builder gapAreas(List<String> gapAreas) { this.gapAreas = gapAreas; return this; }

        public BenchmarkResponse build() {
            return new BenchmarkResponse(supplierId, supplierCode, supplierName, categoryId, categoryName, supplierScore, categoryAverageScore, overallAverageScore, categoryDelta, overallDelta, percentileRankInCategory, percentileRankOverall, totalSuppliersInCategory, totalSuppliersOverall, criteriaBenchmarks, strengthAreas, gapAreas);
        }
    }

    public static class CriteriaBenchmarkItem {
        private String criteriaName;
        private Double supplierScore;
        private Double categoryAverage;
        private Double overallAverage;
        private Double deltaFromCategory;
        private String performanceStatus;

        public CriteriaBenchmarkItem() {}

        public CriteriaBenchmarkItem(String criteriaName, Double supplierScore, Double categoryAverage, Double overallAverage, Double deltaFromCategory, String performanceStatus) {
            this.criteriaName = criteriaName;
            this.supplierScore = supplierScore;
            this.categoryAverage = categoryAverage;
            this.overallAverage = overallAverage;
            this.deltaFromCategory = deltaFromCategory;
            this.performanceStatus = performanceStatus;
        }

        public static ItemBuilder builder() {
            return new ItemBuilder();
        }

        public static class ItemBuilder {
            private String criteriaName;
            private Double supplierScore;
            private Double categoryAverage;
            private Double overallAverage;
            private Double deltaFromCategory;
            private String performanceStatus;

            public ItemBuilder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
            public ItemBuilder supplierScore(Double supplierScore) { this.supplierScore = supplierScore; return this; }
            public ItemBuilder categoryAverage(Double categoryAverage) { this.categoryAverage = categoryAverage; return this; }
            public ItemBuilder overallAverage(Double overallAverage) { this.overallAverage = overallAverage; return this; }
            public ItemBuilder deltaFromCategory(Double deltaFromCategory) { this.deltaFromCategory = deltaFromCategory; return this; }
            public ItemBuilder performanceStatus(String performanceStatus) { this.performanceStatus = performanceStatus; return this; }

            public CriteriaBenchmarkItem build() {
                return new CriteriaBenchmarkItem(criteriaName, supplierScore, categoryAverage, overallAverage, deltaFromCategory, performanceStatus);
            }
        }

        public String getCriteriaName() { return criteriaName; }
        public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
        public Double getSupplierScore() { return supplierScore; }
        public void setSupplierScore(Double supplierScore) { this.supplierScore = supplierScore; }
        public Double getCategoryAverage() { return categoryAverage; }
        public void setCategoryAverage(Double categoryAverage) { this.categoryAverage = categoryAverage; }
        public Double getOverallAverage() { return overallAverage; }
        public void setOverallAverage(Double overallAverage) { this.overallAverage = overallAverage; }
        public Double getDeltaFromCategory() { return deltaFromCategory; }
        public void setDeltaFromCategory(Double deltaFromCategory) { this.deltaFromCategory = deltaFromCategory; }
        public String getPerformanceStatus() { return performanceStatus; }
        public void setPerformanceStatus(String performanceStatus) { this.performanceStatus = performanceStatus; }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Double getSupplierScore() { return supplierScore; }
    public void setSupplierScore(Double supplierScore) { this.supplierScore = supplierScore; }
    public Double getCategoryAverageScore() { return categoryAverageScore; }
    public void setCategoryAverageScore(Double categoryAverageScore) { this.categoryAverageScore = categoryAverageScore; }
    public Double getOverallAverageScore() { return overallAverageScore; }
    public void setOverallAverageScore(Double overallAverageScore) { this.overallAverageScore = overallAverageScore; }
    public Double getCategoryDelta() { return categoryDelta; }
    public void setCategoryDelta(Double categoryDelta) { this.categoryDelta = categoryDelta; }
    public Double getOverallDelta() { return overallDelta; }
    public void setOverallDelta(Double overallDelta) { this.overallDelta = overallDelta; }
    public Double getPercentileRankInCategory() { return percentileRankInCategory; }
    public void setPercentileRankInCategory(Double percentileRankInCategory) { this.percentileRankInCategory = percentileRankInCategory; }
    public Double getPercentileRankOverall() { return percentileRankOverall; }
    public void setPercentileRankOverall(Double percentileRankOverall) { this.percentileRankOverall = percentileRankOverall; }
    public Integer getTotalSuppliersInCategory() { return totalSuppliersInCategory; }
    public void setTotalSuppliersInCategory(Integer totalSuppliersInCategory) { this.totalSuppliersInCategory = totalSuppliersInCategory; }
    public Integer getTotalSuppliersOverall() { return totalSuppliersOverall; }
    public void setTotalSuppliersOverall(Integer totalSuppliersOverall) { this.totalSuppliersOverall = totalSuppliersOverall; }
    public List<CriteriaBenchmarkItem> getCriteriaBenchmarks() { return criteriaBenchmarks; }
    public void setCriteriaBenchmarks(List<CriteriaBenchmarkItem> criteriaBenchmarks) { this.criteriaBenchmarks = criteriaBenchmarks; }
    public List<String> getStrengthAreas() { return strengthAreas; }
    public void setStrengthAreas(List<String> strengthAreas) { this.strengthAreas = strengthAreas; }
    public List<String> getGapAreas() { return gapAreas; }
    public void setGapAreas(List<String> gapAreas) { this.gapAreas = gapAreas; }
}
