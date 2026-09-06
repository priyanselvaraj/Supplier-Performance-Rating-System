package com.supplier.sprsystem.dto.ai;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AiCopilotQueryResponse {

    private Long id;
    private String question;
    private String answer;
    private String queryIntent;
    private String intentCategory;
    private String confidence;
    private boolean sufficientData;
    private List<String> dataCitations;
    private List<Map<String, Object>> relevantData;
    private List<String> suggestedActions;
    private List<String> followUpQuestions;
    private LocalDateTime createdAt;

    public AiCopilotQueryResponse() {}

    public AiCopilotQueryResponse(Long id, String question, String answer, String queryIntent, String intentCategory, String confidence, boolean sufficientData, List<String> dataCitations, List<Map<String, Object>> relevantData, List<String> suggestedActions, List<String> followUpQuestions, LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.queryIntent = queryIntent;
        this.intentCategory = intentCategory;
        this.confidence = confidence;
        this.sufficientData = sufficientData;
        this.dataCitations = dataCitations;
        this.relevantData = relevantData;
        this.suggestedActions = suggestedActions;
        this.followUpQuestions = followUpQuestions;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String question;
        private String answer;
        private String queryIntent;
        private String intentCategory;
        private String confidence;
        private boolean sufficientData = true;
        private List<String> dataCitations;
        private List<Map<String, Object>> relevantData;
        private List<String> suggestedActions;
        private List<String> followUpQuestions;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder question(String question) { this.question = question; return this; }
        public Builder answer(String answer) { this.answer = answer; return this; }
        public Builder queryIntent(String queryIntent) { this.queryIntent = queryIntent; return this; }
        public Builder intentCategory(String intentCategory) { this.intentCategory = intentCategory; return this; }
        public Builder confidence(String confidence) { this.confidence = confidence; return this; }
        public Builder sufficientData(boolean sufficientData) { this.sufficientData = sufficientData; return this; }
        public Builder dataCitations(List<String> dataCitations) { this.dataCitations = dataCitations; return this; }
        public Builder relevantData(List<Map<String, Object>> relevantData) { this.relevantData = relevantData; return this; }
        public Builder suggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; return this; }
        public Builder followUpQuestions(List<String> followUpQuestions) { this.followUpQuestions = followUpQuestions; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AiCopilotQueryResponse build() {
            return new AiCopilotQueryResponse(id, question, answer, queryIntent, intentCategory, confidence, sufficientData, dataCitations, relevantData, suggestedActions, followUpQuestions, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getQueryIntent() { return queryIntent; }
    public void setQueryIntent(String queryIntent) { this.queryIntent = queryIntent; }
    public String getIntentCategory() { return intentCategory; }
    public void setIntentCategory(String intentCategory) { this.intentCategory = intentCategory; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public boolean isSufficientData() { return sufficientData; }
    public void setSufficientData(boolean sufficientData) { this.sufficientData = sufficientData; }
    public List<String> getDataCitations() { return dataCitations; }
    public void setDataCitations(List<String> dataCitations) { this.dataCitations = dataCitations; }
    public List<Map<String, Object>> getRelevantData() { return relevantData; }
    public void setRelevantData(List<Map<String, Object>> relevantData) { this.relevantData = relevantData; }
    public List<String> getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; }
    public List<String> getFollowUpQuestions() { return followUpQuestions; }
    public void setFollowUpQuestions(List<String> followUpQuestions) { this.followUpQuestions = followUpQuestions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
