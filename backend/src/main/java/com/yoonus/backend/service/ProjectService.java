package com.yoonus.backend.service;

import com.yoonus.backend.dto.CreateProjectRequest;
import com.yoonus.backend.dto.ProjectResponse;
import com.yoonus.backend.dto.UpdateProjectRequest;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(String email, CreateProjectRequest request);

    ProjectResponse updateProject(Long projectId, String email, UpdateProjectRequest request);

    void deleteProject(Long projectId, String email);

    ProjectResponse getProject(Long projectId, String email);

    List<ProjectResponse> getAllProjects(String email);
}
