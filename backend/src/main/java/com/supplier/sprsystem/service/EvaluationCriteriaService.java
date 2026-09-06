package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.EvaluationCriteriaRequest;
import com.supplier.sprsystem.dto.response.EvaluationCriteriaResponse;

import java.util.List;

public interface EvaluationCriteriaService {
    List<EvaluationCriteriaResponse> getAllCriteria();
    List<EvaluationCriteriaResponse> getActiveCriteria();
    EvaluationCriteriaResponse getCriteriaById(Long id);
    EvaluationCriteriaResponse createCriteria(EvaluationCriteriaRequest criteriaRequest);
    EvaluationCriteriaResponse updateCriteria(Long id, EvaluationCriteriaRequest criteriaRequest);
    EvaluationCriteriaResponse activateCriteria(Long id);
    EvaluationCriteriaResponse deactivateCriteria(Long id);
    EvaluationCriteriaResponse toggleCriteriaActive(Long id);
    Double getTotalActiveWeights();
    void deleteCriteria(Long id);
}
