package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.AlertSeverity;
import com.supplier.sprsystem.model.entity.AlertStatus;
import com.supplier.sprsystem.model.entity.AlertType;

import java.time.LocalDate;

public class AiAlertResponse {
    private String id;
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private AlertType alertType;
    private AlertSeverity severity;
    private String title;
    private String message;
    private LocalDate triggeredDate;
    private AlertStatus status;

    public AiAlertResponse() {}

    public AiAlertResponse(String id, Long supplierId, String supplierName, String supplierCode, AlertType alertType, AlertSeverity severity, String title, String message, LocalDate triggeredDate, AlertStatus status) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierCode = supplierCode;
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.triggeredDate = triggeredDate;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Long supplierId;
        private String supplierName;
        private String supplierCode;
        private AlertType alertType;
        private AlertSeverity severity;
        private String title;
        private String message;
        private LocalDate triggeredDate;
        private AlertStatus status;

        public Builder id(String id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder alertType(AlertType alertType) { this.alertType = alertType; return this; }
        public Builder severity(AlertSeverity severity) { this.severity = severity; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder triggeredDate(LocalDate triggeredDate) { this.triggeredDate = triggeredDate; return this; }
        public Builder status(AlertStatus status) { this.status = status; return this; }

        public AiAlertResponse build() {
            return new AiAlertResponse(id, supplierId, supplierName, supplierCode, alertType, severity, title, message, triggeredDate, status);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDate getTriggeredDate() { return triggeredDate; }
    public void setTriggeredDate(LocalDate triggeredDate) { this.triggeredDate = triggeredDate; }
    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
}
