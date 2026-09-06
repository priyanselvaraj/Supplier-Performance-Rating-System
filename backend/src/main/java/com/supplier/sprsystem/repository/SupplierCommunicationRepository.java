package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.SupplierCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierCommunicationRepository extends JpaRepository<SupplierCommunication, Long> {

    List<SupplierCommunication> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    List<SupplierCommunication> findBySupplierIdAndRelatedResourceTypeAndRelatedResourceIdOrderByCreatedAtAsc(
            Long supplierId, String relatedResourceType, Long relatedResourceId);
}
