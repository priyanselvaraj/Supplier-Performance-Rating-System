package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.*;
import com.supplier.sprsystem.dto.response.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SupplierPortalService {

    // 1. Dashboard & Profile
    SupplierPortalDashboardResponse getSupplierDashboard();
    SupplierPortalProfileResponse getSupplierProfile();
    SupplierProfileUpdateRequestDto submitProfileUpdateRequest(SupplierProfileUpdateSubmitRequest request);

    // 2. Performance & Evaluations
    SupplierPortalPerformanceResponse getSupplierPerformance();
    List<SupplierPortalEvaluationResponse> getSupplierEvaluations();
    SupplierPortalEvaluationResponse getSupplierEvaluationDetails(Long evaluationId);

    // 3. Improvement Actions
    List<ImprovementActionResponse> getSupplierImprovementActions();
    ImprovementActionResponse respondToImprovementAction(Long actionId, SupplierActionResponseRequest request);

    // 4. Documents
    List<SupplierDocumentResponse> getSupplierDocuments();
    SupplierDocumentResponse uploadDocument(MultipartFile file, String documentType, String notes);
    Resource downloadDocument(Long documentId);
    void deleteDocument(Long documentId);

    // 5. Communications
    List<SupplierCommunicationResponse> getSupplierCommunications();
    SupplierCommunicationResponse sendCommunication(SupplierCommunicationCreateRequest request);

    // 6. Notifications
    List<NotificationResponse> getSupplierNotifications();

    // 7. Admin / Manager Operations
    UserResponse createSupplierUser(SupplierAccountCreateRequest request);
    List<SupplierProfileUpdateRequestDto> getPendingProfileUpdateRequests();
    SupplierProfileUpdateRequestDto reviewProfileUpdateRequest(Long requestId, ProfileUpdateRequestReviewDto reviewDto);
    SupplierDocumentResponse reviewDocument(Long documentId, DocumentReviewDto reviewDto);
}
