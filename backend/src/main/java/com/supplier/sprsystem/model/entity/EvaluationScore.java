package com.supplier.sprsystem.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "evaluation_scores",
        indexes = {
                @Index(name = "idx_eval_score_evaluation_id", columnList = "evaluation_id"),
                @Index(name = "idx_eval_score_criteria_id", columnList = "criteria_id")
        })
public class EvaluationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    @JsonBackReference
    private SupplierEvaluation evaluation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criteria_id", nullable = false)
    private EvaluationCriteria criteria;

    @NotNull
    @Column(name = "score_obtained", nullable = false)
    private Double scoreObtained;

    @NotNull
    @Column(name = "max_score", nullable = false)
    private Double maxScore = 100.0;

    @NotNull
    @Column(nullable = false)
    private Double weight;

    @NotNull
    @Column(name = "weighted_score", nullable = false)
    private Double weightedScore;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public EvaluationScore() {}

    public EvaluationScore(Long id, SupplierEvaluation evaluation, EvaluationCriteria criteria, Double scoreObtained, Double maxScore, Double weight, Double weightedScore, String remarks) {
        this.id = id;
        this.evaluation = evaluation;
        this.criteria = criteria;
        this.scoreObtained = scoreObtained;
        this.maxScore = maxScore != null ? maxScore : 100.0;
        this.weight = weight;
        this.weightedScore = weightedScore;
        this.remarks = remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private SupplierEvaluation evaluation;
        private EvaluationCriteria criteria;
        private Double scoreObtained;
        private Double maxScore = 100.0;
        private Double weight;
        private Double weightedScore;
        private String remarks;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder evaluation(SupplierEvaluation evaluation) { this.evaluation = evaluation; return this; }
        public Builder criteria(EvaluationCriteria criteria) { this.criteria = criteria; return this; }
        public Builder scoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; return this; }
        public Builder score(Double score) { this.scoreObtained = score; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder weight(Double weight) { this.weight = weight; return this; }
        public Builder weightedScore(Double weightedScore) { this.weightedScore = weightedScore; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }
        public Builder comments(String comments) { this.remarks = comments; return this; }

        public EvaluationScore build() {
            return new EvaluationScore(id, evaluation, criteria, scoreObtained, maxScore, weight, weightedScore, remarks);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SupplierEvaluation getEvaluation() { return evaluation; }
    public void setEvaluation(SupplierEvaluation evaluation) { this.evaluation = evaluation; }
    public EvaluationCriteria getCriteria() { return criteria; }
    public void setCriteria(EvaluationCriteria criteria) { this.criteria = criteria; }
    public Double getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; }
    public Double getScore() { return scoreObtained; }
    public void setScore(Double score) { this.scoreObtained = score; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getComments() { return remarks; }
    public void setComments(String comments) { this.remarks = comments; }
}
