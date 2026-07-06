package com.yoonus.backend.repository;

import com.yoonus.backend.entity.GeneratedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedFileRepository extends JpaRepository<GeneratedFile, Long> {
    List<GeneratedFile> findByProjectId(Long projectId);
}