package com.yoonus.backend.dto;

import com.yoonus.backend.entity.WorkspaceRole;

public class ProjectMemberResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private WorkspaceRole role;
    private boolean active;
    private String status;

    public ProjectMemberResponse() {
    }

    public ProjectMemberResponse(Long id, Long userId, String name, String email, WorkspaceRole role, boolean active, String status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public WorkspaceRole getRole() { return role; }
    public void setRole(WorkspaceRole role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
