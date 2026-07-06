package com.yoonus.backend.dto;

import com.yoonus.backend.entity.JobStatus;

import java.time.LocalDateTime;

public class GenerationJobResponse {
    private Long id;
    private String title;
    private String prompt;
    private String framework;
    private String description;
    private JobStatus status;
    private int progress;
    private String result;
    private String errorMessage;
    private String logs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GenerationJobResponse() {
    }

    public GenerationJobResponse(Long id, String title, String prompt, String framework, String description,
                                 JobStatus status, int progress, String result, String errorMessage,
                                 String logs, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.prompt = prompt;
        this.framework = framework;
        this.description = description;
        this.status = status;
        this.progress = progress;
        this.result = result;
        this.errorMessage = errorMessage;
        this.logs = logs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getLogs() { return logs; }
    public void setLogs(String logs) { this.logs = logs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
