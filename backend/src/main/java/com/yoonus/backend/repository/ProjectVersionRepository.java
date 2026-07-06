package com.yoonus.backend.repository;

import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectVersionRepository extends JpaRepository<ProjectVersion, Long> {

    List<ProjectVersion> findAllByProjectOrderByVersionNumberDesc(Project project);

    Optional<ProjectVersion> findFirstByProjectOrderByVersionNumberDesc(Project project);
}
