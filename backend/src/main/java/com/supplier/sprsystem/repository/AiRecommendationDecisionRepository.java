package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.AiRecommendationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiRecommendationDecisionRepository extends JpaRepository<AiRecommendationDecision, Long> {

    List<AiRecommendationDecision> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    Optional<AiRecommendationDecision> findByRecommendationRef(String recommendationRef);

    List<AiRecommendationDecision> findTop20ByOrderByCreatedAtDesc();
}
