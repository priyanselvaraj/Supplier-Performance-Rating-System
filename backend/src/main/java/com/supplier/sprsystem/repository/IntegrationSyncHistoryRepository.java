package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.IntegrationSyncHistory;
import com.supplier.sprsystem.model.entity.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationSyncHistoryRepository extends JpaRepository<IntegrationSyncHistory, Long> {

    List<IntegrationSyncHistory> findAllByOrderByStartedAtDesc();

    List<IntegrationSyncHistory> findTop20ByOrderByStartedAtDesc();

    long countByStatus(SyncStatus status);
}
