package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WorkflowInstance;
import com.supplier.sprsystem.model.entity.WorkflowStatus;
import com.supplier.sprsystem.model.entity.WorkflowType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

    List<WorkflowInstance> findByStatus(WorkflowStatus status);

    List<WorkflowInstance> findByWorkflowType(WorkflowType workflowType);

    List<WorkflowInstance> findByRelatedResourceTypeAndRelatedResourceId(String relatedResourceType, Long relatedResourceId);

    Optional<WorkflowInstance> findFirstByRelatedResourceTypeAndRelatedResourceIdAndStatusIn(
            String relatedResourceType, Long relatedResourceId, List<WorkflowStatus> statuses);

    @Query("SELECT w FROM WorkflowInstance w WHERE " +
            "(:workflowType IS NULL OR w.workflowType = :workflowType) AND " +
            "(:status IS NULL OR w.status = :status) AND " +
            "(:startDate IS NULL OR w.startedAt >= :startDate) AND " +
            "(:endDate IS NULL OR w.startedAt <= :endDate) AND " +
            "(:keyword IS NULL OR LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(w.initiatedBy.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(w.initiatedBy.username) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<WorkflowInstance> searchAndFilter(
            @Param("workflowType") WorkflowType workflowType,
            @Param("status") WorkflowStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT w FROM WorkflowInstance w WHERE " +
            "w.initiatedBy.id = :userId AND " +
            "(:status IS NULL OR w.status = :status)")
    Page<WorkflowInstance> findByInitiatedBy(@Param("userId") Long userId, @Param("status") WorkflowStatus status, Pageable pageable);

    long countByStatus(WorkflowStatus status);

    @Query("SELECT COUNT(DISTINCT t.workflowInstance) FROM ApprovalTask t WHERE t.status = 'PENDING' AND t.dueAt < CURRENT_TIMESTAMP")
    long countOverdueWorkflows();

    @Query("SELECT COUNT(w) FROM WorkflowInstance w WHERE w.status = 'ESCALATED'")
    long countEscalatedWorkflows();
}
