package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.KpiCategory;
import com.supplier.sprsystem.model.entity.KpiDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KpiDefinitionRepository extends JpaRepository<KpiDefinition, Long> {

    Optional<KpiDefinition> findByKpiCode(String kpiCode);

    boolean existsByKpiCode(String kpiCode);

    List<KpiDefinition> findByActiveTrue();

    List<KpiDefinition> findByCategory(KpiCategory category);

    List<KpiDefinition> findByCategoryAndActiveTrue(KpiCategory category);
}
