package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SupplierActionResponseRequest {

    @NotBlank(message = "Response message or update notes are required")
    private String responseNotes;

    public SupplierActionResponseRequest() {}

    public SupplierActionResponseRequest(String responseNotes) {
        this.responseNotes = responseNotes;
    }

    public String getResponseNotes() { return responseNotes; }
    public void setResponseNotes(String responseNotes) { this.responseNotes = responseNotes; }
}
