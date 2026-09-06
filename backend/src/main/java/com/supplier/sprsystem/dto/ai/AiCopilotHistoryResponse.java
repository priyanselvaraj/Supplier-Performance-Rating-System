package com.supplier.sprsystem.dto.ai;

import java.time.LocalDateTime;

public class AiCopilotHistoryResponse {

    private Long id;
    private String question;
    private String responseSummary;
    private String queryIntent;
    private String intentCategory;
    private Boolean helpful;
    private String feedbackReason;
    private LocalDateTime createdAt;

    public AiCopilotHistoryResponse() {}

    public AiCopilotHistoryResponse(Long id, String question, String responseSummary, String queryIntent, String intentCategory, Boolean helpful, String feedbackReason, LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.responseSummary = responseSummary;
        this.queryIntent = queryIntent;
        this.intentCategory = intentCategory;
        this.helpful = helpful;
        this.feedbackReason = feedbackReason;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String question;
        private String responseSummary;
        private String queryIntent;
        private String intentCategory;
        private Boolean helpful;
        private String feedbackReason;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder question(String question) { this.question = question; return this; }
        public Builder responseSummary(String responseSummary) { this.responseSummary = responseSummary; return this; }
        public Builder queryIntent(String queryIntent) { this.queryIntent = queryIntent; return this; }
        public Builder intentCategory(String intentCategory) { this.intentCategory = intentCategory; return this; }
        public Builder helpful(Boolean helpful) { this.helpful = helpful; return this; }
        public Builder feedbackReason(String feedbackReason) { this.feedbackReason = feedbackReason; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AiCopilotHistoryResponse build() {
            return new AiCopilotHistoryResponse(id, question, responseSummary, queryIntent, intentCategory, helpful, feedbackReason, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getResponseSummary() { return responseSummary; }
    public void setResponseSummary(String responseSummary) { this.responseSummary = responseSummary; }
    public String getQueryIntent() { return queryIntent; }
    public void setQueryIntent(String queryIntent) { this.queryIntent = queryIntent; }
    public String getIntentCategory() { return intentCategory; }
    public void setIntentCategory(String intentCategory) { this.intentCategory = intentCategory; }
    public Boolean getHelpful() { return helpful; }
    public void setHelpful(Boolean helpful) { this.helpful = helpful; }
    public String getFeedbackReason() { return feedbackReason; }
    public void setFeedbackReason(String feedbackReason) { this.feedbackReason = feedbackReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
