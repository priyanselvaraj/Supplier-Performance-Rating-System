package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {

    List<WorkflowStep> findByWorkflowDefinitionIdOrderByStepOrderAsc(Long workflowDefinitionId);

    Optional<WorkflowStep> findByWorkflowDefinitionIdAndStepOrder(Long workflowDefinitionId, int stepOrder);

    void deleteByWorkflowDefinitionId(Long workflowDefinitionId);
}
