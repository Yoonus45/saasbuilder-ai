package com.yoonus.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class AiReviewRequest {

    private Long projectId;

    private List<ReviewFileInput> files = new ArrayList<>();

    @NotBlank(message = "Review type is required")
    private String reviewType;

    public AiReviewRequest() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<ReviewFileInput> getFiles() {
        return files;
    }

    public void setFiles(List<ReviewFileInput> files) {
        this.files = files == null ? new ArrayList<>() : files;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public static class ReviewFileInput {
        private String path;
        private String code;
        private String language;

        public ReviewFileInput() {
        }

        public ReviewFileInput(String path, String code, String language) {
            this.path = path;
            this.code = code;
            this.language = language;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
