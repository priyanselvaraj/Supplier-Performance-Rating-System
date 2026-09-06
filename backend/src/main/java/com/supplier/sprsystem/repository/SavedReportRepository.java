package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.SavedReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedReportRepository extends JpaRepository<SavedReport, Long> {

    @Query("SELECT r FROM SavedReport r WHERE r.createdBy.id = :userId OR r.isPublic = true ORDER BY r.updatedAt DESC")
    List<SavedReport> findAccessibleReports(@Param("userId") Long userId);

    @Query("SELECT r FROM SavedReport r WHERE r.createdBy.id = :userId OR r.isPublic = true")
    Page<SavedReport> findAccessibleReportsPage(@Param("userId") Long userId, Pageable pageable);

    List<SavedReport> findByCreatedByIdOrderByUpdatedAtDesc(Long userId);

    long countByCreatedById(Long userId);
}
