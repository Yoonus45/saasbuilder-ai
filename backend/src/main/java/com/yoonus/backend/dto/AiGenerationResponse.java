package com.yoonus.backend.dto;

import com.yoonus.backend.entity.ProjectStatus;

public class AiGenerationResponse {

    private Long projectId;
    private String title;
    private String message;
    private ProjectStatus status;
    private String generatedCode;

    public AiGenerationResponse() {
    }

    public AiGenerationResponse(Long projectId, String title, String message, ProjectStatus status, String generatedCode) {
        this.projectId = projectId;
        this.title = title;
        this.message = message;
        this.status = status;
        this.generatedCode = generatedCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }
}
