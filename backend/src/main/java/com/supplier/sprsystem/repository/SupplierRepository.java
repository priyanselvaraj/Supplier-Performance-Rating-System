package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.Supplier;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierCode(String supplierCode);

    Optional<Supplier> findByEmail(String email);

    boolean existsBySupplierCode(String supplierCode);

    boolean existsByEmail(String email);

    long countByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    List<Supplier> findByStatus(SupplierStatus status);

    List<Supplier> findByCategoryId(Long categoryId);

    List<Supplier> findByRatingCategory(RatingCategory ratingCategory);

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Supplier> searchSuppliers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:city IS NULL OR LOWER(s.city) = LOWER(:city)) AND " +
            "(:country IS NULL OR LOWER(s.country) = LOWER(:country))")
    Page<Supplier> filterSuppliers(
            @Param("categoryId") Long categoryId,
            @Param("status") SupplierStatus status,
            @Param("city") String city,
            @Param("country") String country,
            Pageable pageable
    );

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:ratingCategory IS NULL OR s.ratingCategory = :ratingCategory OR " +
            " (:ratingCategory = com.supplier.sprsystem.model.entity.RatingCategory.UNRATED AND (s.ratingCategory IS NULL OR s.totalEvaluations = 0)))")
    Page<Supplier> searchAndFilterSuppliers(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") SupplierStatus status,
            @Param("ratingCategory") RatingCategory ratingCategory,
            Pageable pageable
    );

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:ratingCategory IS NULL OR s.ratingCategory = :ratingCategory)")
    List<Supplier> filterSuppliersList(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") SupplierStatus status,
            @Param("ratingCategory") RatingCategory ratingCategory
    );

    long countByStatus(SupplierStatus status);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE (:active = true AND s.status = 'ACTIVE') OR (:active = false AND s.status != 'ACTIVE')")
    long countByActive(@Param("active") boolean active);

    long countByRatingCategory(RatingCategory ratingCategory);

    @Query("SELECT AVG(s.overallRating) FROM Supplier s WHERE s.totalEvaluations > 0")
    Double calculateAverageSupplierRating();

    List<Supplier> findTop5ByOrderByOverallRatingDesc();

    List<Supplier> findTop5ByTotalEvaluationsGreaterThanOrderByOverallRatingAsc(int totalEvaluations);
}
