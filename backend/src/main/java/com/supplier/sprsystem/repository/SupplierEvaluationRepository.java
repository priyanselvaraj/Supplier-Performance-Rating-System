package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierEvaluation;
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
public interface SupplierEvaluationRepository extends JpaRepository<SupplierEvaluation, Long> {

    Optional<SupplierEvaluation> findByEvaluationCode(String evaluationCode);

    boolean existsByEvaluationCode(String evaluationCode);

    List<SupplierEvaluation> findBySupplierIdOrderByEvaluationDateDesc(Long supplierId);

    List<SupplierEvaluation> findBySupplierIdAndStatusOrderByEvaluationDateDesc(Long supplierId, EvaluationStatus status);

    List<SupplierEvaluation> findBySupplierIdAndStatusOrderByEvaluationDateAsc(Long supplierId, EvaluationStatus status);

    List<SupplierEvaluation> findByEvaluatorIdOrderByEvaluationDateDesc(Long evaluatorId);

    Page<SupplierEvaluation> findByEvaluatorId(Long evaluatorId, Pageable pageable);

    @Query("SELECT e FROM SupplierEvaluation e WHERE " +
            "(:supplierId IS NULL OR e.supplier.id = :supplierId) AND " +
            "(:evaluatorId IS NULL OR e.evaluator.id = :evaluatorId) AND " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:ratingCategory IS NULL OR e.ratingCategory = :ratingCategory) AND " +
            "(:startDate IS NULL OR e.evaluationDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.evaluationDate <= :endDate)")
    Page<SupplierEvaluation> filterEvaluations(
            @Param("supplierId") Long supplierId,
            @Param("evaluatorId") Long evaluatorId,
            @Param("status") EvaluationStatus status,
            @Param("ratingCategory") RatingCategory ratingCategory,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT e FROM SupplierEvaluation e WHERE " +
            "(:supplierId IS NULL OR e.supplier.id = :supplierId) AND " +
            "(:ratingCategory IS NULL OR e.ratingCategory = :ratingCategory) AND " +
            "(:startDate IS NULL OR e.evaluationDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.evaluationDate <= :endDate)")
    Page<SupplierEvaluation> searchAndFilterEvaluations(
            @Param("supplierId") Long supplierId,
            @Param("ratingCategory") RatingCategory ratingCategory,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT e FROM SupplierEvaluation e WHERE " +
            "(:supplierId IS NULL OR e.supplier.id = :supplierId) AND " +
            "(:ratingCategory IS NULL OR e.ratingCategory = :ratingCategory) AND " +
            "(:startDate IS NULL OR e.evaluationDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.evaluationDate <= :endDate)")
    List<SupplierEvaluation> filterEvaluationsList(
            @Param("supplierId") Long supplierId,
            @Param("ratingCategory") RatingCategory ratingCategory,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<SupplierEvaluation> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT AVG(e.totalWeightedScore) FROM SupplierEvaluation e WHERE e.supplier.id = :supplierId AND e.status != 'CANCELLED'")
    Double calculateAverageScoreBySupplierId(@Param("supplierId") Long supplierId);

    long countByRatingCategory(RatingCategory ratingCategory);

    long countByStatus(EvaluationStatus status);
}
