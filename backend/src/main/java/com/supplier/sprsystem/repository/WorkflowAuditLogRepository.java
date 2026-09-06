package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WorkflowAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowAuditLogRepository extends JpaRepository<WorkflowAuditLog, Long> {

    List<WorkflowAuditLog> findByWorkflowInstanceIdOrderByTimestampAsc(Long workflowInstanceId);
}
