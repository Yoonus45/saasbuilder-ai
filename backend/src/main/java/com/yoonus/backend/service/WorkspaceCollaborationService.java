package com.yoonus.backend.service;

import com.yoonus.backend.dto.InviteMemberRequest;
import com.yoonus.backend.dto.ProjectMemberResponse;
import com.yoonus.backend.dto.ProjectMemberUpdateRequest;
import com.yoonus.backend.entity.*;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.repository.WorkspaceInvitationRepository;
import com.yoonus.backend.repository.WorkspaceMemberRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkspaceCollaborationService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public WorkspaceCollaborationService(
            WorkspaceMemberRepository workspaceMemberRepository,
            WorkspaceInvitationRepository workspaceInvitationRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceInvitationRepository = workspaceInvitationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> listMembers(Long projectId, User currentUser) {
        Project project = getProject(projectId);
        ensureMember(project, currentUser);
        List<WorkspaceMember> members = workspaceMemberRepository.findByProject(project);
        if (members.isEmpty() && (project.getUser() != null && project.getUser().getId().equals(currentUser.getId()))) {
            WorkspaceMember ownerMembership = new WorkspaceMember();
            ownerMembership.setProject(project);
            ownerMembership.setUser(currentUser);
            ownerMembership.setRole(WorkspaceRole.OWNER);
            ownerMembership.setActive(true);
            workspaceMemberRepository.save(ownerMembership);
            members = List.of(ownerMembership);
        }
        return members.stream()
                .filter(WorkspaceMember::isActive)
                .map(member -> new ProjectMemberResponse(
                        member.getId(),
                        member.getUser().getId(),
                        member.getUser().getName(),
                        member.getUser().getEmail(),
                        member.getRole(),
                        member.isActive(),
                        "ACTIVE"))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectMemberResponse inviteMember(Long projectId, InviteMemberRequest request, User currentUser) {
        Project project = getProject(projectId);
        ensureCanManage(project, currentUser);

        Optional<User> invitedUser = userRepository.findByEmail(request.getEmail());
        if (invitedUser.isPresent()) {
            Optional<WorkspaceMember> existingMembership = workspaceMemberRepository.findByProjectAndUser(project, invitedUser.get());
            if (existingMembership.isPresent()) {
                return new ProjectMemberResponse(existingMembership.get().getId(), invitedUser.get().getId(), invitedUser.get().getName(), invitedUser.get().getEmail(), existingMembership.get().getRole(), existingMembership.get().isActive(), "EXISTS");
            }
            WorkspaceMember member = new WorkspaceMember();
            member.setProject(project);
            member.setUser(invitedUser.get());
            member.setRole(parseRole(request.getRole()));
            member.setActive(true);
            workspaceMemberRepository.save(member);
            return new ProjectMemberResponse(member.getId(), invitedUser.get().getId(), invitedUser.get().getName(), invitedUser.get().getEmail(), member.getRole(), member.isActive(), "ADDED");
        }

        WorkspaceInvitation invitation = new WorkspaceInvitation();
        invitation.setProject(project);
        invitation.setInviter(currentUser);
        invitation.setEmail(request.getEmail());
        invitation.setRole(parseRole(request.getRole()));
        invitation.setStatus(InvitationStatus.PENDING);
        workspaceInvitationRepository.save(invitation);
        return new ProjectMemberResponse(null, null, request.getEmail(), request.getEmail(), parseRole(request.getRole()), true, "INVITED");
    }

    @Transactional
    public ProjectMemberResponse updateMemberRole(Long projectId, Long memberId, ProjectMemberUpdateRequest request, User currentUser) {
        Project project = getProject(projectId);
        ensureCanManage(project, currentUser);
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        if (!member.getProject().getId().equals(projectId)) {
            throw new AccessDeniedException("Member does not belong to this project");
        }
        member.setRole(parseRole(request.getRole()));
        workspaceMemberRepository.save(member);
        return new ProjectMemberResponse(member.getId(), member.getUser().getId(), member.getUser().getName(), member.getUser().getEmail(), member.getRole(), member.isActive(), "UPDATED");
    }

    @Transactional
    public void removeMember(Long projectId, Long memberId, User currentUser) {
        Project project = getProject(projectId);
        ensureCanManage(project, currentUser);
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        if (!member.getProject().getId().equals(projectId)) {
            throw new AccessDeniedException("Member does not belong to this project");
        }
        workspaceMemberRepository.delete(member);
    }

    @Transactional
    public void acceptInvitation(Long invitationId, User currentUser) {
        WorkspaceInvitation invitation = workspaceInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (!invitation.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new AccessDeniedException("This invitation is not for you");
        }
        invitation.setStatus(InvitationStatus.ACCEPTED);
        workspaceInvitationRepository.save(invitation);

        Optional<User> invitedUser = userRepository.findByEmail(currentUser.getEmail());
        if (invitedUser.isPresent()) {
            Optional<WorkspaceMember> existingMembership = workspaceMemberRepository.findByProjectAndUser(invitation.getProject(), invitedUser.get());
            if (existingMembership.isEmpty()) {
                WorkspaceMember member = new WorkspaceMember();
                member.setProject(invitation.getProject());
                member.setUser(invitedUser.get());
                member.setRole(invitation.getRole());
                member.setActive(true);
                workspaceMemberRepository.save(member);
            }
        }
    }

    @Transactional
    public void rejectInvitation(Long invitationId, User currentUser) {
        WorkspaceInvitation invitation = workspaceInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (!invitation.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new AccessDeniedException("This invitation is not for you");
        }
        invitation.setStatus(InvitationStatus.REJECTED);
        workspaceInvitationRepository.save(invitation);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> listInvitations(Long projectId, User currentUser) {
        Project project = getProject(projectId);
        ensureCanManage(project, currentUser);
        return workspaceInvitationRepository.findByProject(project).stream()
                .map(invitation -> new ProjectMemberResponse(
                        invitation.getId(),
                        null,
                        invitation.getEmail(),
                        invitation.getEmail(),
                        invitation.getRole(),
                        true,
                        invitation.getStatus().name()))
                .collect(Collectors.toList());
    }

    public boolean canEdit(Project project, User user) {
        return canAccess(project, user, WorkspaceRole.EDITOR, WorkspaceRole.ADMIN, WorkspaceRole.OWNER);
    }

    public boolean canView(Project project, User user) {
        return canAccess(project, user, WorkspaceRole.VIEWER, WorkspaceRole.EDITOR, WorkspaceRole.ADMIN, WorkspaceRole.OWNER);
    }

    private boolean canAccess(Project project, User user, WorkspaceRole... allowedRoles) {
        if (project == null || user == null) {
            return false;
        }
        if (project.getUser() != null && project.getUser().getId().equals(user.getId())) {
            return true;
        }
        Optional<WorkspaceMember> membership = workspaceMemberRepository.findByProjectAndUser(project, user);
        if (membership.isPresent()) {
            WorkspaceRole role = membership.get().getRole();
            for (WorkspaceRole allowedRole : allowedRoles) {
                if (allowedRole == role) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ensureMember(Project project, User user) {
        if (!canView(project, user)) {
            throw new AccessDeniedException("You are not a member of this project");
        }
    }

    private void ensureCanManage(Project project, User user) {
        if (!canManage(project, user)) {
            throw new AccessDeniedException("You do not have permission to manage the project team");
        }
    }

    private boolean canManage(Project project, User user) {
        if (project == null || user == null) {
            return false;
        }
        if (project.getUser() != null && project.getUser().getId().equals(user.getId())) {
            return true;
        }
        Optional<WorkspaceMember> membership = workspaceMemberRepository.findByProjectAndUser(project, user);
        if (membership.isPresent()) {
            WorkspaceRole role = membership.get().getRole();
            return role == WorkspaceRole.ADMIN || role == WorkspaceRole.OWNER;
        }
        return false;
    }

    private WorkspaceRole parseRole(String role) {
        if (role == null) {
            return WorkspaceRole.EDITOR;
        }
        try {
            return WorkspaceRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return WorkspaceRole.EDITOR;
        }
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }
}
