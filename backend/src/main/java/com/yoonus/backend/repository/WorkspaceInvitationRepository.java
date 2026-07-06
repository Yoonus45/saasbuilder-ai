package com.yoonus.backend.repository;

import com.yoonus.backend.entity.InvitationStatus;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.WorkspaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, Long> {
    List<WorkspaceInvitation> findByProject(Project project);
    Optional<WorkspaceInvitation> findByProjectAndEmailAndStatus(Project project, String email, InvitationStatus status);
    List<WorkspaceInvitation> findByEmailAndStatus(String email, InvitationStatus status);
}
