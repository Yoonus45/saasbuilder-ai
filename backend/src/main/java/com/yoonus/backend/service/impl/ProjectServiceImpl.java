package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.CreateProjectRequest;
import com.yoonus.backend.dto.ProjectResponse;
import com.yoonus.backend.dto.UpdateProjectRequest;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.exception.UnauthorizedAccessException;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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
        project.setUser(owner);

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, String email, UpdateProjectRequest request) {
        User owner = findUserByEmail(email);
        Project project = projectRepository.findByIdAndUser(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setPrompt(request.getPrompt());
        project.setGeneratedCode(request.getGeneratedCode());
        project.setFramework(request.getFramework());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long projectId, String email) {
        User owner = findUserByEmail(email);
        Project project = projectRepository.findByIdAndUser(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));

        projectRepository.delete(project);
    }

    @Override
    public ProjectResponse getProject(Long projectId, String email) {
        User owner = findUserByEmail(email);
        Project project = projectRepository.findByIdAndUser(projectId, owner)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));

        return mapToResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects(String email) {
        User owner = findUserByEmail(email);
        return projectRepository.findAllByUser(owner)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
