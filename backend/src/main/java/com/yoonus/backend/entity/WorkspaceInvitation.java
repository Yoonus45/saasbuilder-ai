package com.yoonus.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_invitations")
public class WorkspaceInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceRole role = WorkspaceRole.EDITOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public WorkspaceInvitation() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getInviter() { return inviter; }
    public void setInviter(User inviter) { this.inviter = inviter; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public WorkspaceRole getRole() { return role; }
    public void setRole(WorkspaceRole role) { this.role = role; }
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
