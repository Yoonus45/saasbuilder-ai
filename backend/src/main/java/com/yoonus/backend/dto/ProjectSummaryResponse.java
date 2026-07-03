package com.yoonus.backend.dto;

import com.yoonus.backend.entity.ProjectStatus;

public class ProjectSummaryResponse {

    private Long id;
    private String title;
    private ProjectStatus status;

    public ProjectSummaryResponse() {
    }

    public ProjectSummaryResponse(Long id, String title, ProjectStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
