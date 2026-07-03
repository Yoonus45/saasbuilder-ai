package com.yoonus.backend.dto;

import java.time.LocalDateTime;

public class AiGenerationHistoryResponse {

    private Long id;
    private String title;
    private String prompt;
    private String generatedCode;
    private String framework;
    private LocalDateTime createdAt;

    public AiGenerationHistoryResponse() {
    }

    public AiGenerationHistoryResponse(Long id, String title, String prompt, String generatedCode, String framework, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.prompt = prompt;
        this.generatedCode = generatedCode;
        this.framework = framework;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
