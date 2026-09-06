package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ImprovementActionRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotBlank(message = "Action title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Action description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private ImprovementActionPriority priority = ImprovementActionPriority.MEDIUM;
    private ImprovementActionStatus status = ImprovementActionStatus.OPEN;

    private Long assignedUserId;
    private LocalDate dueDate;
    private String resolutionNotes;

    public ImprovementActionRequest() {}

    public ImprovementActionRequest(Long supplierId, String title, String description, ImprovementActionPriority priority, ImprovementActionStatus status, Long assignedUserId, LocalDate dueDate, String resolutionNotes) {
        this.supplierId = supplierId;
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : ImprovementActionPriority.MEDIUM;
        this.status = status != null ? status : ImprovementActionStatus.OPEN;
        this.assignedUserId = assignedUserId;
        this.dueDate = dueDate;
        this.resolutionNotes = resolutionNotes;
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ImprovementActionPriority getPriority() { return priority; }
    public void setPriority(ImprovementActionPriority priority) { this.priority = priority; }
    public ImprovementActionStatus getStatus() { return status; }
    public void setStatus(ImprovementActionStatus status) { this.status = status; }
    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
