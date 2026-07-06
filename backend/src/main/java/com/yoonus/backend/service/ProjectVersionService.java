package com.yoonus.backend.service;

import com.yoonus.backend.dto.ProjectVersionResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.ProjectVersion;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.ProjectVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectVersionService {

    private final ProjectRepository projectRepository;
    private final ProjectVersionRepository projectVersionRepository;

    public ProjectVersionService(ProjectRepository projectRepository, ProjectVersionRepository projectVersionRepository) {
        this.projectRepository = projectRepository;
        this.projectVersionRepository = projectVersionRepository;
    }

    @Transactional
    public ProjectVersionResponse createVersion(Long projectId, String summary, String prompt, String generatedCode, String framework, ProjectStatus status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        int nextVersion = Math.max(1, project.getGenerationCount() + 1);
        project.setGenerationCount(nextVersion);
        projectRepository.save(project);

        ProjectVersion version = new ProjectVersion();
        version.setProject(project);
        version.setVersionNumber(nextVersion);
        version.setSummary(summary);
        version.setPrompt(prompt);
        version.setGeneratedCode(generatedCode);
        version.setFramework(framework);
        version.setStatus(status);

        ProjectVersion savedVersion = projectVersionRepository.save(version);
        return mapToResponse(savedVersion);
    }

    @Transactional(readOnly = true)
    public List<ProjectVersionResponse> listVersions(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return projectVersionRepository.findAllByProjectOrderByVersionNumberDesc(project)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectVersionResponse getVersion(Long projectId, Long versionId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        ProjectVersion version = projectVersionRepository.findById(versionId)
                .filter(item -> item.getProject().getId().equals(project.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
        return mapToResponse(version);
    }

    @Transactional
    public ProjectVersionResponse restoreVersion(Long projectId, Long versionId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        ProjectVersion version = projectVersionRepository.findById(versionId)
                .filter(item -> item.getProject().getId().equals(project.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));

        project.setGeneratedCode(version.getGeneratedCode());
        project.setPrompt(version.getPrompt());
        project.setFramework(version.getFramework());
        project.setStatus(version.getStatus());
        projectRepository.save(project);

        return mapToResponse(version);
    }

    private ProjectVersionResponse mapToResponse(ProjectVersion version) {
        return new ProjectVersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getSummary(),
                version.getPrompt(),
                version.getGeneratedCode(),
                version.getFramework(),
                version.getStatus(),
                version.getCreatedAt()
        );
    }
}
