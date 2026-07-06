package com.yoonus.backend.dto;

import com.yoonus.backend.entity.ProjectStatus;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String prompt;
    private String generatedCode;
    private String framework;
    private ProjectStatus status;
    private boolean favorite;
    private boolean pinned;
    private boolean archived;
    private String tags;
    private int generationCount;
    private String deploymentStatus;
    private String githubStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectResponse() {
    }

    public ProjectResponse(Long id, String title, String description, String prompt, String generatedCode,
                           String framework, ProjectStatus status, boolean favorite, boolean pinned,
                           boolean archived, String tags, int generationCount, String deploymentStatus,
                           String githubStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.prompt = prompt;
        this.generatedCode = generatedCode;
        this.framework = framework;
        this.status = status;
        this.favorite = favorite;
        this.pinned = pinned;
        this.archived = archived;
        this.tags = tags;
        this.generationCount = generationCount;
        this.deploymentStatus = deploymentStatus;
        this.githubStatus = githubStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(int generationCount) {
        this.generationCount = generationCount;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public String getGithubStatus() {
        return githubStatus;
    }

    public void setGithubStatus(String githubStatus) {
        this.githubStatus = githubStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
