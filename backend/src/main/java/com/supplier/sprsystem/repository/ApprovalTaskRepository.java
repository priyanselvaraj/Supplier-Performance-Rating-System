package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.ApprovalTask;
import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.TaskStatus;

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
public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {

    List<ApprovalTask> findByWorkflowInstanceIdOrderByStepOrderAsc(Long workflowInstanceId);

    Optional<ApprovalTask> findFirstByWorkflowInstanceIdAndStatus(Long workflowInstanceId, TaskStatus status);

    @Query("SELECT t FROM ApprovalTask t WHERE " +
            "t.status = 'PENDING' AND " +
            "(t.assignedUser.id = :userId OR (t.assignedUser IS NULL AND t.requiredRole IN :roles))")
    Page<ApprovalTask> findPendingTasksForUser(
            @Param("userId") Long userId,
            @Param("roles") List<ERole> roles,
            Pageable pageable);

    @Query("SELECT t FROM ApprovalTask t WHERE " +
            "(t.assignedUser.id = :userId OR t.actionedBy.id = :userId OR (t.requiredRole IN :roles)) AND " +
            "(:status IS NULL OR t.status = :status)")
    Page<ApprovalTask> findUserApprovalHistory(
            @Param("userId") Long userId,
            @Param("roles") List<ERole> roles,
            @Param("status") TaskStatus status,
            Pageable pageable);

    @Query("SELECT t FROM ApprovalTask t WHERE " +
            "t.status = 'PENDING' AND " +
            "t.dueAt < :now AND " +
            "t.escalated = false")
    List<ApprovalTask> findOverduePendingTasks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM ApprovalTask t WHERE " +
            "t.status = 'PENDING' AND " +
            "(t.assignedUser.id = :userId OR (t.assignedUser IS NULL AND t.requiredRole IN :roles))")
    long countPendingTasksForUser(@Param("userId") Long userId, @Param("roles") List<ERole> roles);

    long countByStatus(TaskStatus status);

    long countByEscalatedTrue();
}
