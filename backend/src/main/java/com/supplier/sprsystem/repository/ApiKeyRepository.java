package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyHash(String keyHash);

    boolean existsByName(String name);

    List<ApiKey> findAllByOrderByCreatedAtDesc();

    long countByActiveTrue();
}
