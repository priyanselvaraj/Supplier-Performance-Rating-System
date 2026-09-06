package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.EvaluationCriteriaRequest;
import com.supplier.sprsystem.dto.response.EvaluationCriteriaResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.EvaluationCriteria;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.EvaluationScoreRepository;
import com.supplier.sprsystem.service.EvaluationCriteriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationCriteriaServiceImpl implements EvaluationCriteriaService {

    private final EvaluationCriteriaRepository criteriaRepository;
    private final EvaluationScoreRepository scoreRepository;

    public EvaluationCriteriaServiceImpl(EvaluationCriteriaRepository criteriaRepository, EvaluationScoreRepository scoreRepository) {
        this.criteriaRepository = criteriaRepository;
        this.scoreRepository = scoreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getAllCriteria() {
        return criteriaRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getActiveCriteria() {
        return criteriaRepository.findByActiveOrderByDisplayOrderAsc(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationCriteriaResponse getCriteriaById(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));
        return mapToResponse(criteria);
    }

    @Override
    @Transactional
    public EvaluationCriteriaResponse createCriteria(EvaluationCriteriaRequest request) {
        if (criteriaRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Criteria already exists with name: " + request.getName());
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (criteriaRepository.existsByCode(request.getCode().trim().toUpperCase())) {
                throw new DuplicateResourceException("Criteria already exists with code: " + request.getCode());
            }
        }

        EvaluationCriteria criteria = EvaluationCriteria.builder()
                .name(request.getName().trim())
                .code(request.getCode() != null && !request.getCode().trim().isEmpty() ? request.getCode().trim().toUpperCase() : null)
                .description(request.getDescription())
                .weight(request.getWeight())
                .maxScore(request.getMaxScore() != null ? request.getMaxScore() : 100.0)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 1)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        EvaluationCriteria saved = criteriaRepository.save(criteria);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EvaluationCriteriaResponse updateCriteria(Long id, EvaluationCriteriaRequest request) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));

        if (!criteria.getName().equalsIgnoreCase(request.getName().trim())
                && criteriaRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Criteria already exists with name: " + request.getName());
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (!request.getCode().equalsIgnoreCase(criteria.getCode())
                    && criteriaRepository.existsByCode(request.getCode().trim().toUpperCase())) {
                throw new DuplicateResourceException("Criteria already exists with code: " + request.getCode());
            }
            criteria.setCode(request.getCode().trim().toUpperCase());
        }

        criteria.setName(request.getName().trim());
        criteria.setDescription(request.getDescription());
        criteria.setWeight(request.getWeight());
        if (request.getMaxScore() != null) {
            criteria.setMaxScore(request.getMaxScore());
        }
        if (request.getDisplayOrder() != null) {
            criteria.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getActive() != null) {
            criteria.setActive(request.getActive());
        }

        EvaluationCriteria updated = criteriaRepository.save(criteria);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationCriteriaResponse activateCriteria(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));
        criteria.setActive(true);
        EvaluationCriteria updated = criteriaRepository.save(criteria);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationCriteriaResponse deactivateCriteria(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));
        criteria.setActive(false);
        EvaluationCriteria updated = criteriaRepository.save(criteria);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationCriteriaResponse toggleCriteriaActive(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));

        criteria.setActive(!criteria.isActive());
        EvaluationCriteria updated = criteriaRepository.save(criteria);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalActiveWeights() {
        return criteriaRepository.sumActiveWeights();
    }

    @Override
    @Transactional
    public void deleteCriteria(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", id));

        if (!scoreRepository.findByCriteriaId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete criteria because evaluation scores are associated with it. Please deactivate the criteria instead.");
        }

        criteriaRepository.delete(criteria);
    }

    private EvaluationCriteriaResponse mapToResponse(EvaluationCriteria criteria) {
        return EvaluationCriteriaResponse.builder()
                .id(criteria.getId())
                .name(criteria.getName())
                .code(criteria.getCode())
                .description(criteria.getDescription())
                .weight(criteria.getWeight())
                .maxScore(criteria.getMaxScore())
                .displayOrder(criteria.getDisplayOrder())
                .active(criteria.isActive())
                .createdAt(criteria.getCreatedAt())
                .updatedAt(criteria.getUpdatedAt())
                .build();
    }
}
