package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.AiInteractionHistory;
import com.supplier.sprsystem.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiInteractionHistoryRepository extends JpaRepository<AiInteractionHistory, Long> {

    List<AiInteractionHistory> findByUserOrderByCreatedAtDesc(User user);

    List<AiInteractionHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<AiInteractionHistory> findTop30ByUserOrderByCreatedAtDesc(User user);

    List<AiInteractionHistory> findTop50ByOrderByCreatedAtDesc();
}
