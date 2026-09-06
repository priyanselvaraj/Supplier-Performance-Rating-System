package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.SupplierCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long> {
    Optional<SupplierCategory> findByName(String name);
    Optional<SupplierCategory> findByNameIgnoreCase(String name);
    Optional<SupplierCategory> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByCode(String code);
    List<SupplierCategory> findByActive(boolean active);
}
