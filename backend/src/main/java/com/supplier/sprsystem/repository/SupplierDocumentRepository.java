package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.DocumentStatus;
import com.supplier.sprsystem.model.entity.SupplierDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierDocumentRepository extends JpaRepository<SupplierDocument, Long> {

    List<SupplierDocument> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    List<SupplierDocument> findBySupplierIdAndStatusOrderByCreatedAtDesc(Long supplierId, DocumentStatus status);

    Optional<SupplierDocument> findByIdAndSupplierId(Long id, Long supplierId);

    long countBySupplierId(Long supplierId);

    long countBySupplierIdAndStatus(Long supplierId, DocumentStatus status);
}
