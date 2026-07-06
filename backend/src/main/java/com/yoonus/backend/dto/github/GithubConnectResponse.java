package com.yoonus.backend.dto.github;

public class GithubConnectResponse {
    private String authUrl;
    private String message;

    public GithubConnectResponse() {
    }

    public GithubConnectResponse(String authUrl, String message) {
        this.authUrl = authUrl;
        this.message = message;
    }

    public String getAuthUrl() { return authUrl; }
    public void setAuthUrl(String authUrl) { this.authUrl = authUrl; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
