package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {

    Optional<EvaluationCriteria> findByName(String name);

    Optional<EvaluationCriteria> findByNameIgnoreCase(String name);

    Optional<EvaluationCriteria> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCode(String code);

    List<EvaluationCriteria> findByActive(boolean active);

    List<EvaluationCriteria> findByActiveOrderByDisplayOrderAsc(boolean active);

    List<EvaluationCriteria> findByActiveTrueOrderByDisplayOrderAsc();

    List<EvaluationCriteria> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT COALESCE(SUM(c.weight), 0.0) FROM EvaluationCriteria c WHERE c.active = true")
    Double sumActiveWeights();
}
