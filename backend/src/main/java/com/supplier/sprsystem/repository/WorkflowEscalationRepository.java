package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WorkflowEscalation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowEscalationRepository extends JpaRepository<WorkflowEscalation, Long> {

    List<WorkflowEscalation> findByWorkflowInstanceIdOrderByEscalatedAtDesc(Long workflowInstanceId);

    List<WorkflowEscalation> findByResolvedFalseOrderByEscalatedAtDesc();

    Page<WorkflowEscalation> findAllByOrderByEscalatedAtDesc(Pageable pageable);

    boolean existsByApprovalTaskId(Long approvalTaskId);
}
