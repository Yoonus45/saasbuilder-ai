package com.yoonus.backend.dto.github;

import java.time.LocalDateTime;

public class GithubProfileResponse {
    private String username;
    private String avatarUrl;
    private LocalDateTime connectedAt;

    public GithubProfileResponse() {
    }

    public GithubProfileResponse(String username, String avatarUrl, LocalDateTime connectedAt) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.connectedAt = connectedAt;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public LocalDateTime getConnectedAt() { return connectedAt; }
    public void setConnectedAt(LocalDateTime connectedAt) { this.connectedAt = connectedAt; }
}
