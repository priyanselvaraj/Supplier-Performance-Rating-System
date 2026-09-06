package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.response.EvaluationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.RatingCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SupplierEvaluationService {

    EvaluationResponse createEvaluation(EvaluationRequest request);

    EvaluationResponse createDraftEvaluation(EvaluationRequest request);

    PaginatedResponse<EvaluationResponse> getEvaluations(
            Long supplierId, Long evaluatorId, EvaluationStatus status, RatingCategory ratingCategory,
            LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String direction
    );

    PaginatedResponse<EvaluationResponse> getMyEvaluations(int page, int size, String sortBy, String direction);

    List<EvaluationResponse> getAllEvaluations();

    EvaluationResponse getEvaluationById(Long id);

    EvaluationResponse getEvaluationByCode(String code);

    List<EvaluationResponse> getEvaluationsBySupplierId(Long supplierId);

    EvaluationResponse updateDraftEvaluation(Long id, EvaluationRequest request);

    EvaluationResponse submitEvaluation(Long id);

    EvaluationResponse submitEvaluation(EvaluationRequest request);

    EvaluationResponse completeEvaluation(Long id);

    EvaluationResponse cancelEvaluation(Long id);

    void deleteEvaluation(Long id);

    Page<EvaluationResponse> searchAndFilterEvaluations(
            Long supplierId, RatingCategory ratingCategory, LocalDate startDate, LocalDate endDate, Pageable pageable
    );
}
