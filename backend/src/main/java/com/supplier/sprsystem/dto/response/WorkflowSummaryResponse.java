package com.supplier.sprsystem.dto.response;

public class WorkflowSummaryResponse {
    private long totalWorkflows;
    private long pendingApprovals;
    private long inProgressWorkflows;
    private long approvedWorkflows;
    private long rejectedWorkflows;
    private long overdueTasks;
    private long escalatedWorkflows;
    private long myPendingTasks;

    public WorkflowSummaryResponse() {}

    public WorkflowSummaryResponse(long totalWorkflows, long pendingApprovals, long inProgressWorkflows, long approvedWorkflows, long rejectedWorkflows, long overdueTasks, long escalatedWorkflows, long myPendingTasks) {
        this.totalWorkflows = totalWorkflows;
        this.pendingApprovals = pendingApprovals;
        this.inProgressWorkflows = inProgressWorkflows;
        this.approvedWorkflows = approvedWorkflows;
        this.rejectedWorkflows = rejectedWorkflows;
        this.overdueTasks = overdueTasks;
        this.escalatedWorkflows = escalatedWorkflows;
        this.myPendingTasks = myPendingTasks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalWorkflows;
        private long pendingApprovals;
        private long inProgressWorkflows;
        private long approvedWorkflows;
        private long rejectedWorkflows;
        private long overdueTasks;
        private long escalatedWorkflows;
        private long myPendingTasks;

        public Builder totalWorkflows(long totalWorkflows) { this.totalWorkflows = totalWorkflows; return this; }
        public Builder pendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; return this; }
        public Builder inProgressWorkflows(long inProgressWorkflows) { this.inProgressWorkflows = inProgressWorkflows; return this; }
        public Builder approvedWorkflows(long approvedWorkflows) { this.approvedWorkflows = approvedWorkflows; return this; }
        public Builder rejectedWorkflows(long rejectedWorkflows) { this.rejectedWorkflows = rejectedWorkflows; return this; }
        public Builder overdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; return this; }
        public Builder escalatedWorkflows(long escalatedWorkflows) { this.escalatedWorkflows = escalatedWorkflows; return this; }
        public Builder myPendingTasks(long myPendingTasks) { this.myPendingTasks = myPendingTasks; return this; }

        public WorkflowSummaryResponse build() {
            return new WorkflowSummaryResponse(totalWorkflows, pendingApprovals, inProgressWorkflows, approvedWorkflows, rejectedWorkflows, overdueTasks, escalatedWorkflows, myPendingTasks);
        }
    }

    public long getTotalWorkflows() { return totalWorkflows; }
    public void setTotalWorkflows(long totalWorkflows) { this.totalWorkflows = totalWorkflows; }
    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }
    public long getInProgressWorkflows() { return inProgressWorkflows; }
    public void setInProgressWorkflows(long inProgressWorkflows) { this.inProgressWorkflows = inProgressWorkflows; }
    public long getApprovedWorkflows() { return approvedWorkflows; }
    public void setApprovedWorkflows(long approvedWorkflows) { this.approvedWorkflows = approvedWorkflows; }
    public long getRejectedWorkflows() { return rejectedWorkflows; }
    public void setRejectedWorkflows(long rejectedWorkflows) { this.rejectedWorkflows = rejectedWorkflows; }
    public long getOverdueTasks() { return overdueTasks; }
    public void setOverdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; }
    public long getEscalatedWorkflows() { return escalatedWorkflows; }
    public void setEscalatedWorkflows(long escalatedWorkflows) { this.escalatedWorkflows = escalatedWorkflows; }
    public long getMyPendingTasks() { return myPendingTasks; }
    public void setMyPendingTasks(long myPendingTasks) { this.myPendingTasks = myPendingTasks; }
}
