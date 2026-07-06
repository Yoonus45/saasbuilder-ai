package com.yoonus.backend.dto.github;

public class GithubRepoResponse {
    private String name;
    private String fullName;
    private String description;
    private String defaultBranch;
    private String htmlUrl;

    public GithubRepoResponse() {
    }

    public GithubRepoResponse(String name, String fullName, String description, String defaultBranch, String htmlUrl) {
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.defaultBranch = defaultBranch;
        this.htmlUrl = htmlUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }
    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
}
