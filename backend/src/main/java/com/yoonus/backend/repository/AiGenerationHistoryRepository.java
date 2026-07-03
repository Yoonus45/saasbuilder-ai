package com.yoonus.backend.repository;

import com.yoonus.backend.entity.AiGenerationHistory;
import com.yoonus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiGenerationHistoryRepository extends JpaRepository<AiGenerationHistory, Long> {

    List<AiGenerationHistory> findAllByUserOrderByCreatedAtDesc(User user);
}
