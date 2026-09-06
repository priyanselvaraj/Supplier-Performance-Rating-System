package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.SupplierProfileUpdateRequest;
import com.supplier.sprsystem.model.entity.UpdateRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierProfileUpdateRequestRepository extends JpaRepository<SupplierProfileUpdateRequest, Long> {

    List<SupplierProfileUpdateRequest> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    List<SupplierProfileUpdateRequest> findByStatusOrderByCreatedAtDesc(UpdateRequestStatus status);

    Optional<SupplierProfileUpdateRequest> findByIdAndSupplierId(Long id, Long supplierId);

    boolean existsBySupplierIdAndStatus(Long supplierId, UpdateRequestStatus status);
}
