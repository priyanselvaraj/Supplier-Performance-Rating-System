package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierPerformanceRating;
import com.supplier.sprsystem.model.entity.SupplierRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierPerformanceRatingRepository extends JpaRepository<SupplierPerformanceRating, Long> {

    Optional<SupplierPerformanceRating> findByEvaluationId(Long evaluationId);

    boolean existsByEvaluationId(Long evaluationId);

    List<SupplierPerformanceRating> findBySupplierIdOrderByRatingDateDescCreatedAtDesc(Long supplierId);

    Optional<SupplierPerformanceRating> findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(Long supplierId);

    List<SupplierPerformanceRating> findByPerformanceStatusOrderByRatingDateDesc(PerformanceStatus performanceStatus);

    @Query("SELECT r FROM SupplierPerformanceRating r WHERE " +
            "(:supplierId IS NULL OR r.supplier.id = :supplierId) AND " +
            "(:rating IS NULL OR r.rating = :rating) AND " +
            "(:performanceStatus IS NULL OR r.performanceStatus = :performanceStatus) AND " +
            "(:startDate IS NULL OR r.ratingDate >= :startDate) AND " +
            "(:endDate IS NULL OR r.ratingDate <= :endDate)")
    Page<SupplierPerformanceRating> filterRatings(
            @Param("supplierId") Long supplierId,
            @Param("rating") SupplierRating rating,
            @Param("performanceStatus") PerformanceStatus performanceStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    long countByRating(SupplierRating rating);

    long countByPerformanceStatus(PerformanceStatus performanceStatus);
}
