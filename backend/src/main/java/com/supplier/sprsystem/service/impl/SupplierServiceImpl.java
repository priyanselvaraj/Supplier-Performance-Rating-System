package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.SupplierRequest;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.SupplierCategoryResponse;
import com.supplier.sprsystem.dto.response.SupplierResponse;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.Supplier;
import com.supplier.sprsystem.model.entity.SupplierCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository categoryRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierCategoryRepository categoryRepository) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SupplierResponse> getSuppliers(
            String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<Supplier> supplierPage = supplierRepository.searchAndFilterSuppliers(
                cleanKeyword, categoryId, status, ratingCategory, pageable);
        return PaginatedResponse.fromPage(supplierPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SupplierResponse> getAllSuppliers(int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Supplier> supplierPage = supplierRepository.findAll(pageable);
        return PaginatedResponse.fromPage(supplierPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> searchAndFilterSuppliers(
            String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<Supplier> page = supplierRepository.searchAndFilterSuppliers(
                cleanKeyword, categoryId, status, ratingCategory, pageable);
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SupplierResponse> searchSuppliers(String keyword, int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<Supplier> supplierPage = supplierRepository.searchSuppliers(cleanKeyword, pageable);
        return PaginatedResponse.fromPage(supplierPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SupplierResponse> filterSuppliers(Long categoryId, Boolean active, String city, String country, int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        SupplierStatus status = null;
        if (active != null) {
            status = active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE;
        }
        String cleanCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String cleanCountry = (country != null && !country.trim().isEmpty()) ? country.trim() : null;

        Page<Supplier> supplierPage = supplierRepository.filterSuppliers(categoryId, status, cleanCity, cleanCountry, pageable);
        return PaginatedResponse.fromPage(supplierPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return mapToResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierByCode(String code) {
        Supplier supplier = supplierRepository.findBySupplierCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "supplierCode", code));
        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        SupplierCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", request.getCategoryId()));

        String code = generateUniqueSupplierCode();

        if (supplierRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Supplier already exists with email: " + request.getEmail());
        }

        SupplierStatus initialStatus = SupplierStatus.ACTIVE;
        if (request.getStatus() != null) {
            initialStatus = request.getStatus();
        } else if (request.getActive() != null) {
            initialStatus = request.getActive() ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE;
        }

        Supplier supplier = Supplier.builder()
                .supplierCode(code)
                .name(request.getName().trim())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone())
                .address(request.getAddress())
                .website(request.getWebsite())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .category(category)
                .status(initialStatus)
                .overallRating(0.0)
                .ratingCategory(RatingCategory.UNRATED)
                .totalEvaluations(0)
                .build();

        Supplier saved = supplierRepository.save(supplier);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        SupplierCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("SupplierCategory", "id", request.getCategoryId()));

        if (!supplier.getEmail().equalsIgnoreCase(request.getEmail().trim())
                && supplierRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Supplier already exists with email: " + request.getEmail());
        }

        supplier.setName(request.getName().trim());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail().trim().toLowerCase());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setWebsite(request.getWebsite());
        supplier.setCity(request.getCity());
        supplier.setState(request.getState());
        supplier.setCountry(request.getCountry());
        supplier.setCategory(category);

        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        } else if (request.getActive() != null) {
            supplier.setStatus(request.getActive() ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE);
        }

        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplierStatus(Long id, SupplierStatus status) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplier.setStatus(status);
        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SupplierResponse activateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setStatus(SupplierStatus.ACTIVE);
        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SupplierResponse deactivateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setStatus(SupplierStatus.INACTIVE);
        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.delete(supplier);
    }

    private synchronized String generateUniqueSupplierCode() {
        long count = supplierRepository.count() + 1;
        String code = String.format("SUP-%05d", count);
        int attempts = 0;
        while (supplierRepository.existsBySupplierCode(code) && attempts < 1000) {
            count++;
            code = String.format("SUP-%05d", count);
            attempts++;
        }
        return code;
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        String cleanSortBy = "name";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String prop = sortBy.trim();
            if (prop.equalsIgnoreCase("supplierName") || prop.equalsIgnoreCase("name")) {
                cleanSortBy = "name";
            } else if (prop.equalsIgnoreCase("supplierCode") || prop.equalsIgnoreCase("code")) {
                cleanSortBy = "supplierCode";
            } else if (prop.equalsIgnoreCase("email")) {
                cleanSortBy = "email";
            } else if (prop.equalsIgnoreCase("contactPerson")) {
                cleanSortBy = "contactPerson";
            } else if (prop.equalsIgnoreCase("status")) {
                cleanSortBy = "status";
            } else if (prop.equalsIgnoreCase("overallRating") || prop.equalsIgnoreCase("rating") || prop.equalsIgnoreCase("ratingScore")) {
                cleanSortBy = "overallRating";
            } else if (prop.equalsIgnoreCase("totalEvaluations") || prop.equalsIgnoreCase("evaluations")) {
                cleanSortBy = "totalEvaluations";
            } else if (prop.equalsIgnoreCase("category") || prop.equalsIgnoreCase("category.name") || prop.equalsIgnoreCase("categoryName")) {
                cleanSortBy = "category.name";
            } else if (prop.equalsIgnoreCase("createdAt")) {
                cleanSortBy = "createdAt";
            } else {
                cleanSortBy = prop;
            }
        }

        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        return PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(sortDirection, cleanSortBy));
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        SupplierCategoryResponse catResponse = null;
        if (supplier.getCategory() != null) {
            catResponse = SupplierCategoryResponse.builder()
                    .id(supplier.getCategory().getId())
                    .name(supplier.getCategory().getName())
                    .code(supplier.getCategory().getCode())
                    .description(supplier.getCategory().getDescription())
                    .active(supplier.getCategory().isActive())
                    .createdAt(supplier.getCategory().getCreatedAt())
                    .updatedAt(supplier.getCategory().getUpdatedAt())
                    .build();
        }

        return SupplierResponse.builder()
                .id(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .name(supplier.getName())
                .supplierName(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .phoneNumber(supplier.getPhone())
                .address(supplier.getAddress())
                .website(supplier.getWebsite())
                .city(supplier.getCity())
                .state(supplier.getState())
                .country(supplier.getCountry())
                .category(catResponse)
                .status(supplier.getStatus())
                .active(supplier.isActive())
                .overallRating(supplier.getOverallRating())
                .ratingCategory(supplier.getRatingCategory())
                .totalEvaluations(supplier.getTotalEvaluations())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
