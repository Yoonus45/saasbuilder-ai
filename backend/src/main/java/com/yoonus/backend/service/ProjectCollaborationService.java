package com.yoonus.backend.service;

import com.yoonus.backend.dto.ProjectMemberResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.WorkspaceMember;
import com.yoonus.backend.entity.WorkspaceRole;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.repository.WorkspaceInvitationRepository;
import com.yoonus.backend.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectCollaborationService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;

    public ProjectCollaborationService(ProjectRepository projectRepository,
                                       UserRepository userRepository,
                                       WorkspaceMemberRepository workspaceMemberRepository,
                                       WorkspaceInvitationRepository workspaceInvitationRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceInvitationRepository = workspaceInvitationRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(Long projectId, String email) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return List.of();
        }

        Project project = projectOpt.get();
        User owner = project.getUser();
        if (owner == null) {
            Optional<User> resolvedOwner = userRepository.findByEmail(email);
            if (resolvedOwner.isPresent()) {
                owner = resolvedOwner.get();
            } else {
                owner = new User();
                owner.setEmail(email);
                owner.setName(email);
            }
        }

        List<WorkspaceMember> members = workspaceMemberRepository.findByProject(project);
        if (members.isEmpty()) {
            WorkspaceMember ownerMembership = new WorkspaceMember();
            ownerMembership.setProject(project);
            ownerMembership.setUser(owner);
            ownerMembership.setRole(WorkspaceRole.OWNER);
            ownerMembership.setActive(true);
            workspaceMemberRepository.save(ownerMembership);
            return List.of(new ProjectMemberResponse(ownerMembership.getId(), owner.getId(), owner.getName(), owner.getEmail(), WorkspaceRole.OWNER, true, "ACTIVE"));
        }

        List<ProjectMemberResponse> response = new ArrayList<>();
        for (WorkspaceMember member : members) {
            response.add(new ProjectMemberResponse(
                    member.getId(),
                    member.getUser().getId(),
                    member.getUser().getName(),
                    member.getUser().getEmail(),
                    member.getRole(),
                    member.isActive(),
                    "ACTIVE"
            ));
        }
        return response;
    }
}
