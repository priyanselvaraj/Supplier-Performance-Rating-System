package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.EvaluationScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationScoreRepository extends JpaRepository<EvaluationScore, Long> {

    List<EvaluationScore> findByEvaluationId(Long evaluationId);

    List<EvaluationScore> findByCriteriaId(Long criteriaId);

    @Query("SELECT AVG(s.scoreObtained) FROM EvaluationScore s WHERE s.criteria.id = :criteriaId")
    Double calculateAverageScoreByCriteriaId(@Param("criteriaId") Long criteriaId);
}
