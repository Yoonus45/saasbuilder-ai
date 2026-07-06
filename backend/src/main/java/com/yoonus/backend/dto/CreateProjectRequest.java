package com.yoonus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateProjectRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Prompt is required")
    private String prompt;

    @NotBlank(message = "Framework is required")
    private String framework;

    private Boolean favorite;

    private Boolean pinned;

    private Boolean archived;

    private String tags;

    private Integer generationCount;

    private String deploymentStatus;

    private String githubStatus;

    public CreateProjectRequest() {
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

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(Integer generationCount) {
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
}
