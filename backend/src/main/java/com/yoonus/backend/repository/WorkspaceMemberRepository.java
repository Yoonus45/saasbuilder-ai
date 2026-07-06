package com.yoonus.backend.repository;

import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.WorkspaceMember;
import com.yoonus.backend.entity.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    List<WorkspaceMember> findByProject(Project project);
    Optional<WorkspaceMember> findByProjectAndUser(Project project, User user);
    boolean existsByProjectAndUser(Project project, User user);
    List<WorkspaceMember> findByProjectAndRole(Project project, WorkspaceRole role);
}
