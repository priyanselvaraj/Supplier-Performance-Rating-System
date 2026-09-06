package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.WorkflowDefinitionRequest;
import com.supplier.sprsystem.dto.request.WorkflowStepRequest;
import com.supplier.sprsystem.dto.response.WorkflowDefinitionResponse;
import com.supplier.sprsystem.dto.response.WorkflowStepResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.WorkflowDefinition;
import com.supplier.sprsystem.model.entity.WorkflowStep;
import com.supplier.sprsystem.model.entity.WorkflowType;
import com.supplier.sprsystem.repository.WorkflowDefinitionRepository;
import com.supplier.sprsystem.repository.WorkflowStepRepository;
import com.supplier.sprsystem.service.WorkflowDefinitionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowStepRepository stepRepository;

    public WorkflowDefinitionServiceImpl(WorkflowDefinitionRepository definitionRepository,
                                         WorkflowStepRepository stepRepository) {
        this.definitionRepository = definitionRepository;
        this.stepRepository = stepRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDefinitionResponse> getAllDefinitions() {
        return definitionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDefinitionResponse> getActiveDefinitions() {
        return definitionRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowDefinitionResponse getDefinitionById(Long id) {
        WorkflowDefinition def = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowDefinition", "id", id));
        return mapToResponse(def);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowDefinitionResponse getDefinitionByType(WorkflowType type) {
        WorkflowDefinition def = definitionRepository.findByWorkflowType(type)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowDefinition", "workflowType", type));
        return mapToResponse(def);
    }

    @Override
    @Transactional
    public WorkflowDefinitionResponse createDefinition(WorkflowDefinitionRequest request) {
        if (definitionRepository.existsByWorkflowType(request.getWorkflowType())) {
            throw new DuplicateResourceException("Workflow definition for type " + request.getWorkflowType() + " already exists.");
        }

        validateStepRequests(request.getSteps());

        WorkflowDefinition definition = WorkflowDefinition.builder()
                .name(request.getName().trim())
                .workflowType(request.getWorkflowType())
                .description(request.getDescription())
                .active(request.isActive())
                .build();

        for (WorkflowStepRequest stepReq : request.getSteps()) {
            WorkflowStep step = WorkflowStep.builder()
                    .workflowDefinition(definition)
                    .stepOrder(stepReq.getStepOrder())
                    .stepName(stepReq.getStepName().trim())
                    .requiredRole(stepReq.getRequiredRole())
                    .slaHours(stepReq.getSlaHours() > 0 ? stepReq.getSlaHours() : 24)
                    .escalationRole(stepReq.getEscalationRole())
                    .autoEscalate(stepReq.isAutoEscalate())
                    .instructions(stepReq.getInstructions())
                    .build();
            definition.addStep(step);
        }

        WorkflowDefinition saved = definitionRepository.save(definition);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public WorkflowDefinitionResponse updateDefinition(Long id, WorkflowDefinitionRequest request) {
        WorkflowDefinition def = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowDefinition", "id", id));

        if (def.getWorkflowType() != request.getWorkflowType() &&
                definitionRepository.existsByWorkflowType(request.getWorkflowType())) {
            throw new DuplicateResourceException("Workflow definition for type " + request.getWorkflowType() + " already exists.");
        }

        validateStepRequests(request.getSteps());

        def.setName(request.getName().trim());
        def.setWorkflowType(request.getWorkflowType());
        def.setDescription(request.getDescription());
        def.setActive(request.isActive());

        // Clear existing steps and re-add
        def.getSteps().clear();
        for (WorkflowStepRequest stepReq : request.getSteps()) {
            WorkflowStep step = WorkflowStep.builder()
                    .workflowDefinition(def)
                    .stepOrder(stepReq.getStepOrder())
                    .stepName(stepReq.getStepName().trim())
                    .requiredRole(stepReq.getRequiredRole())
                    .slaHours(stepReq.getSlaHours() > 0 ? stepReq.getSlaHours() : 24)
                    .escalationRole(stepReq.getEscalationRole())
                    .autoEscalate(stepReq.isAutoEscalate())
                    .instructions(stepReq.getInstructions())
                    .build();
            def.addStep(step);
        }

        WorkflowDefinition updated = definitionRepository.save(def);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public WorkflowDefinitionResponse toggleDefinitionActive(Long id) {
        WorkflowDefinition def = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowDefinition", "id", id));
        def.setActive(!def.isActive());
        return mapToResponse(definitionRepository.save(def));
    }

    @Override
    @Transactional
    public void deleteDefinition(Long id) {
        WorkflowDefinition def = definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowDefinition", "id", id));
        definitionRepository.delete(def);
    }

    private void validateStepRequests(List<WorkflowStepRequest> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new BadRequestException("Workflow definition must have at least one step.");
        }

        Set<Integer> orders = new HashSet<>();
        for (WorkflowStepRequest step : steps) {
            if (step.getStepOrder() < 1) {
                throw new BadRequestException("Step order must be a positive integer starting from 1.");
            }
            if (!orders.add(step.getStepOrder())) {
                throw new BadRequestException("Duplicate step order detected: " + step.getStepOrder());
            }
            if (step.getRequiredRole() == null) {
                throw new BadRequestException("Required role must be specified for step: " + step.getStepName());
            }
            if (step.getSlaHours() <= 0) {
                throw new BadRequestException("SLA hours must be greater than zero for step: " + step.getStepName());
            }
        }

        // Validate contiguous sequence from 1 to N
        for (int i = 1; i <= steps.size(); i++) {
            if (!orders.contains(i)) {
                throw new BadRequestException("Workflow steps must be sequential starting from 1. Missing step order: " + i);
            }
        }
    }

    public WorkflowDefinitionResponse mapToResponse(WorkflowDefinition def) {
        List<WorkflowStepResponse> stepResponses = def.getSteps() != null ? def.getSteps().stream()
                .sorted(Comparator.comparingInt(WorkflowStep::getStepOrder))
                .map(this::mapStepToResponse)
                .collect(Collectors.toList()) : new ArrayList<>();

        return WorkflowDefinitionResponse.builder()
                .id(def.getId())
                .name(def.getName())
                .workflowType(def.getWorkflowType())
                .workflowTypeDisplayName(def.getWorkflowType() != null ? def.getWorkflowType().getDisplayName() : null)
                .description(def.getDescription())
                .active(def.isActive())
                .totalSteps(stepResponses.size())
                .steps(stepResponses)
                .createdAt(def.getCreatedAt())
                .updatedAt(def.getUpdatedAt())
                .build();
    }

    public WorkflowStepResponse mapStepToResponse(WorkflowStep step) {
        return WorkflowStepResponse.builder()
                .id(step.getId())
                .stepOrder(step.getStepOrder())
                .stepName(step.getStepName())
                .requiredRole(step.getRequiredRole())
                .requiredRoleDisplayName(step.getRequiredRole() != null ? step.getRequiredRole().name().replace("ROLE_", "") : null)
                .slaHours(step.getSlaHours())
                .escalationRole(step.getEscalationRole())
                .escalationRoleDisplayName(step.getEscalationRole() != null ? step.getEscalationRole().name().replace("ROLE_", "") : null)
                .autoEscalate(step.isAutoEscalate())
                .instructions(step.getInstructions())
                .build();
    }
}
