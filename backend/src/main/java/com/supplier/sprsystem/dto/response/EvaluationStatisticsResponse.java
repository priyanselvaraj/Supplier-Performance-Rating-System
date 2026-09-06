package com.supplier.sprsystem.dto.response;

public class EvaluationStatisticsResponse {

    private long total;
    private long draft;
    private long submitted;
    private long completed;
    private long cancelled;

    public EvaluationStatisticsResponse() {}

    public EvaluationStatisticsResponse(long total, long draft, long submitted, long completed, long cancelled) {
        this.total = total;
        this.draft = draft;
        this.submitted = submitted;
        this.completed = completed;
        this.cancelled = cancelled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long total;
        private long draft;
        private long submitted;
        private long completed;
        private long cancelled;

        public Builder total(long total) { this.total = total; return this; }
        public Builder draft(long draft) { this.draft = draft; return this; }
        public Builder submitted(long submitted) { this.submitted = submitted; return this; }
        public Builder completed(long completed) { this.completed = completed; return this; }
        public Builder cancelled(long cancelled) { this.cancelled = cancelled; return this; }

        public EvaluationStatisticsResponse build() {
            return new EvaluationStatisticsResponse(total, draft, submitted, completed, cancelled);
        }
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getDraft() { return draft; }
    public void setDraft(long draft) { this.draft = draft; }
    public long getSubmitted() { return submitted; }
    public void setSubmitted(long submitted) { this.submitted = submitted; }
    public long getCompleted() { return completed; }
    public void setCompleted(long completed) { this.completed = completed; }
    public long getCancelled() { return cancelled; }
    public void setCancelled(long cancelled) { this.cancelled = cancelled; }
}
