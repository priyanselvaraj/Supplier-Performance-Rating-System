package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.SupplierCategoryRequest;
import com.supplier.sprsystem.dto.response.SupplierCategoryResponse;

import java.util.List;

public interface SupplierCategoryService {
    List<SupplierCategoryResponse> getAllCategories();
    SupplierCategoryResponse getCategoryById(Long id);
    SupplierCategoryResponse createCategory(SupplierCategoryRequest categoryRequest);
    SupplierCategoryResponse updateCategory(Long id, SupplierCategoryRequest categoryRequest);
    void deleteCategory(Long id);
    SupplierCategoryResponse activateCategory(Long id);
    SupplierCategoryResponse deactivateCategory(Long id);
}
