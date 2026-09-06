package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManualEscalationRequest {

    @NotBlank(message = "Escalation reason is required")
    @Size(max = 255)
    private String reason;

    private ERole targetRole;

    private Long targetUserId;

    public ManualEscalationRequest() {}

    public ManualEscalationRequest(String reason, ERole targetRole, Long targetUserId) {
        this.reason = reason;
        this.targetRole = targetRole;
        this.targetUserId = targetUserId;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public ERole getTargetRole() { return targetRole; }
    public void setTargetRole(ERole targetRole) { this.targetRole = targetRole; }
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
}
