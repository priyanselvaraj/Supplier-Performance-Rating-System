package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;
import com.supplier.sprsystem.model.entity.SupplierImprovementAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierImprovementActionRepository extends JpaRepository<SupplierImprovementAction, Long> {

    List<SupplierImprovementAction> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    @Query("SELECT a FROM SupplierImprovementAction a WHERE " +
            "(:supplierId IS NULL OR a.supplier.id = :supplierId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:priority IS NULL OR a.priority = :priority) AND " +
            "(:assignedUserId IS NULL OR a.assignedUser.id = :assignedUserId) " +
            "ORDER BY a.createdAt DESC")
    Page<SupplierImprovementAction> filterActions(
            @Param("supplierId") Long supplierId,
            @Param("status") ImprovementActionStatus status,
            @Param("priority") ImprovementActionPriority priority,
            @Param("assignedUserId") Long assignedUserId,
            Pageable pageable
    );

    long countByStatus(ImprovementActionStatus status);

    List<SupplierImprovementAction> findTop5ByStatusInOrderByCreatedAtDesc(List<ImprovementActionStatus> statuses);
}
