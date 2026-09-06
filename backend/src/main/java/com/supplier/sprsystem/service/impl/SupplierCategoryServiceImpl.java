package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.SupplierCategoryRequest;
import com.supplier.sprsystem.dto.response.SupplierCategoryResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.SupplierCategory;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.SupplierCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierCategoryServiceImpl implements SupplierCategoryService {

    private final SupplierCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public SupplierCategoryServiceImpl(SupplierCategoryRepository categoryRepository, SupplierRepository supplierRepository) {
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierCategoryResponse getCategoryById(Long id) {
        SupplierCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", id));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public SupplierCategoryResponse createCategory(SupplierCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("A category with this name already exists");
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (categoryRepository.existsByCode(request.getCode().trim().toUpperCase())) {
                throw new DuplicateResourceException("Supplier category already exists with code: " + request.getCode());
            }
        }

        SupplierCategory category = SupplierCategory.builder()
                .name(request.getName().trim())
                .code(request.getCode() != null && !request.getCode().trim().isEmpty() ? request.getCode().trim().toUpperCase() : null)
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        SupplierCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SupplierCategoryResponse updateCategory(Long id, SupplierCategoryRequest request) {
        SupplierCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", id));

        if (!category.getName().equalsIgnoreCase(request.getName().trim())
                && categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("A category with this name already exists");
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (!request.getCode().equalsIgnoreCase(category.getCode())
                    && categoryRepository.existsByCode(request.getCode().trim().toUpperCase())) {
                throw new DuplicateResourceException("Supplier category already exists with code: " + request.getCode());
            }
            category.setCode(request.getCode().trim().toUpperCase());
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        SupplierCategory updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        SupplierCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", id));

        if (supplierRepository.countByCategoryId(id) > 0 || supplierRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Cannot delete category because suppliers are associated with it");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public SupplierCategoryResponse activateCategory(Long id) {
        SupplierCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", id));
        category.setActive(true);
        SupplierCategory updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SupplierCategoryResponse deactivateCategory(Long id) {
        SupplierCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", id));
        category.setActive(false);
        SupplierCategory updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    private SupplierCategoryResponse mapToResponse(SupplierCategory category) {
        return SupplierCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
