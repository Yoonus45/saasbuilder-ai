package com.yoonus.backend.dto;

import com.yoonus.backend.entity.ProjectStatus;

import java.time.LocalDateTime;

public class ProjectVersionResponse {
    private Long id;
    private int versionNumber;
    private String summary;
    private String prompt;
    private String generatedCode;
    private String framework;
    private ProjectStatus status;
    private LocalDateTime createdAt;

    public ProjectVersionResponse() {
    }

    public ProjectVersionResponse(Long id, int versionNumber, String summary, String prompt, String generatedCode, String framework, ProjectStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.versionNumber = versionNumber;
        this.summary = summary;
        this.prompt = prompt;
        this.generatedCode = generatedCode;
        this.framework = framework;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getGeneratedCode() { return generatedCode; }
    public void setGeneratedCode(String generatedCode) { this.generatedCode = generatedCode; }
    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
