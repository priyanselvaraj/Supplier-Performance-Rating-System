package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.SupplierRequest;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.SupplierResponse;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {

    PaginatedResponse<SupplierResponse> getSuppliers(String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, int page, int size, String sortBy, String direction);

    PaginatedResponse<SupplierResponse> getAllSuppliers(int page, int size, String sortBy, String direction);

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse getSupplierById(Long id);

    SupplierResponse getSupplierByCode(String code);

    SupplierResponse createSupplier(SupplierRequest supplierRequest);

    SupplierResponse updateSupplier(Long id, SupplierRequest supplierRequest);

    void deleteSupplier(Long id);

    SupplierResponse activateSupplier(Long id);

    SupplierResponse deactivateSupplier(Long id);

    SupplierResponse updateSupplierStatus(Long id, SupplierStatus status);

    PaginatedResponse<SupplierResponse> searchSuppliers(String keyword, int page, int size, String sortBy, String direction);

    PaginatedResponse<SupplierResponse> filterSuppliers(Long categoryId, Boolean active, String city, String country, int page, int size, String sortBy, String direction);

    Page<SupplierResponse> searchAndFilterSuppliers(String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, Pageable pageable);
}
