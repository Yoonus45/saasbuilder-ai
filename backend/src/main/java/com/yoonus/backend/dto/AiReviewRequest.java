package com.yoonus.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class AiReviewRequest {

    private Long projectId;

    private String fileName;

    private String fileContent;

    private String refactoringType;

    private String testFramework;

    private String documentationType;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getRefactoringType() {
        return refactoringType;
    }

    public void setRefactoringType(String refactoringType) {
        this.refactoringType = refactoringType;
    }

    public String getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }

    public String getDocumentationType() {
        return documentationType;
    }

    public void setDocumentationType(String documentationType) {
        this.documentationType = documentationType;
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
