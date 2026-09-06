package com.supplier.sprsystem.dto.ai;

import jakarta.validation.constraints.NotBlank;

public class AiCopilotQueryRequest {

    @NotBlank(message = "Question cannot be blank")
    private String question;

    private Long supplierId;

    private String context;

    public AiCopilotQueryRequest() {}

    public AiCopilotQueryRequest(String question, Long supplierId, String context) {
        this.question = question;
        this.supplierId = supplierId;
        this.context = context;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
