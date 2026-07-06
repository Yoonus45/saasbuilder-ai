package com.yoonus.backend.repository;

import com.yoonus.backend.entity.Deployment;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByUserOrderByCreatedAtDesc(User user);

    List<Deployment> findByProjectOrderByCreatedAtDesc(Project project);
}
