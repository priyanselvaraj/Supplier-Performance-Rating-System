package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WorkflowDefinition;
import com.supplier.sprsystem.model.entity.WorkflowType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {

    Optional<WorkflowDefinition> findByWorkflowType(WorkflowType workflowType);

    Optional<WorkflowDefinition> findByWorkflowTypeAndActiveTrue(WorkflowType workflowType);

    List<WorkflowDefinition> findByActiveTrue();

    boolean existsByWorkflowType(WorkflowType workflowType);
}
