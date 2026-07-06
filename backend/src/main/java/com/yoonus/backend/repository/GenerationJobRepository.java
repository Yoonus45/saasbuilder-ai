package com.yoonus.backend.repository;

import com.yoonus.backend.entity.GenerationJob;
import com.yoonus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenerationJobRepository extends JpaRepository<GenerationJob, Long> {
    List<GenerationJob> findAllByUserOrderByCreatedAtDesc(User user);
    Optional<GenerationJob> findByIdAndUser(Long id, User user);
}
