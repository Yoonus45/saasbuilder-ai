package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.CreateProjectRequest;
import com.yoonus.backend.dto.ProjectResponse;
import com.yoonus.backend.dto.UpdateProjectRequest;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.WorkspaceMember;
import com.yoonus.backend.entity.WorkspaceRole;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.exception.UnauthorizedAccessException;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.repository.WorkspaceMemberRepository;
import com.yoonus.backend.service.ProjectService;
import com.yoonus.backend.service.WorkspaceCollaborationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceCollaborationService workspaceCollaborationService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              WorkspaceMemberRepository workspaceMemberRepository,
                              WorkspaceCollaborationService workspaceCollaborationService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceCollaborationService = workspaceCollaborationService;
    }

    @Override
    public ProjectResponse createProject(String email, CreateProjectRequest request) {
        User owner = findUserByEmail(email);

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setPrompt(request.getPrompt());
        project.setFramework(request.getFramework());
        project.setStatus(ProjectStatus.CREATED);
        project.setFavorite(Boolean.TRUE.equals(request.getFavorite()));
        project.setPinned(Boolean.TRUE.equals(request.getPinned()));
        project.setArchived(Boolean.TRUE.equals(request.getArchived()));
        project.setTags(request.getTags());
        project.setGenerationCount(request.getGenerationCount() != null ? request.getGenerationCount() : 1);
        project.setDeploymentStatus(request.getDeploymentStatus() != null ? request.getDeploymentStatus() : "Not deployed");
        project.setGithubStatus(request.getGithubStatus() != null ? request.getGithubStatus() : "Not connected");
        project.setUser(owner);

        Project savedProject = projectRepository.save(project);
        WorkspaceMember ownerMembership = new WorkspaceMember();
        ownerMembership.setProject(savedProject);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(WorkspaceRole.OWNER);
        ownerMembership.setActive(true);
        workspaceMemberRepository.save(ownerMembership);
        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, String email, UpdateProjectRequest request) {
        User owner = findUserByEmail(email);
        Project project = findAccessibleProject(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));
        if (!isProjectOwner(project, owner) && !workspaceCollaborationService.canEdit(project, owner)) {
            throw new UnauthorizedAccessException("You do not have permission to edit this project");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setPrompt(request.getPrompt());
        project.setGeneratedCode(request.getGeneratedCode());
        project.setFramework(request.getFramework());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getFavorite() != null) {
            project.setFavorite(request.getFavorite());
        }
        if (request.getPinned() != null) {
            project.setPinned(request.getPinned());
        }
        if (request.getArchived() != null) {
            project.setArchived(request.getArchived());
        }
        if (request.getTags() != null) {
            project.setTags(request.getTags());
        }
        if (request.getGenerationCount() != null) {
            project.setGenerationCount(request.getGenerationCount());
        }
        if (request.getDeploymentStatus() != null) {
            project.setDeploymentStatus(request.getDeploymentStatus());
        }
        if (request.getGithubStatus() != null) {
            project.setGithubStatus(request.getGithubStatus());
        }

        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long projectId, String email) {
        User owner = findUserByEmail(email);
        Project project = findAccessibleProject(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));
        if (!isProjectOwner(project, owner)) {
            throw new UnauthorizedAccessException("You do not have permission to delete this project");
        }

        projectRepository.delete(project);
    }

    @Override
    public ProjectResponse getProject(Long projectId, String email) {
        User owner = findUserByEmail(email);
        Project project = findAccessibleProject(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));
        if (!isProjectOwner(project, owner) && !workspaceCollaborationService.canView(project, owner)) {
            throw new UnauthorizedAccessException("You do not have access to this project");
        }

        return mapToResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects(String email) {
        User owner = findUserByEmail(email);
        List<Project> projects = projectRepository.findAllByUser(owner);
        if (projects == null || projects.isEmpty()) {
            projects = projectRepository.findAll();
        }
        return projects.stream()
                .filter(project -> isOwnerOrMember(project, owner))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isOwnerOrMember(Project project, User user) {
        if (project == null || user == null) {
            return false;
        }
        return isProjectOwner(project, user) || workspaceCollaborationService.canView(project, user);
    }

    private boolean isProjectOwner(Project project, User user) {
        return project != null
                && user != null
                && project.getUser() != null
                && project.getUser().getId() != null
                && project.getUser().getId().equals(user.getId());
    }

    private Optional<Project> findAccessibleProject(Long projectId, User user) {
        Optional<Project> projectById = projectRepository.findById(projectId);
        if (projectById.isPresent() && isOwnerOrMember(projectById.get(), user)) {
            return projectById;
        }
        return projectRepository.findByIdAndUser(projectId, user)
                .filter(project -> isOwnerOrMember(project, user));
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getPrompt(),
                project.getGeneratedCode(),
                project.getFramework(),
                project.getStatus(),
                project.isFavorite(),
                project.isPinned(),
                project.isArchived(),
                project.getTags(),
                project.getGenerationCount(),
                project.getDeploymentStatus(),
                project.getGithubStatus(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
