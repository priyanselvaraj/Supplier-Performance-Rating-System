package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.WorkflowDefinitionRequest;
import com.supplier.sprsystem.dto.response.WorkflowDefinitionResponse;
import com.supplier.sprsystem.model.entity.WorkflowType;

import java.util.List;

public interface WorkflowDefinitionService {

    List<WorkflowDefinitionResponse> getAllDefinitions();

    List<WorkflowDefinitionResponse> getActiveDefinitions();

    WorkflowDefinitionResponse getDefinitionById(Long id);

    WorkflowDefinitionResponse getDefinitionByType(WorkflowType type);

    WorkflowDefinitionResponse createDefinition(WorkflowDefinitionRequest request);

    WorkflowDefinitionResponse updateDefinition(Long id, WorkflowDefinitionRequest request);

    WorkflowDefinitionResponse toggleDefinitionActive(Long id);

    void deleteDefinition(Long id);
}
