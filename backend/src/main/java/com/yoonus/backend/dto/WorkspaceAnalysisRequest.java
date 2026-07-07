package com.yoonus.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceAnalysisRequest {

    private Long projectId;
    private String prompt;
    private String analysisMode;
    private List<String> selectedPaths = new ArrayList<>();
    private List<WorkspaceFileInput> files = new ArrayList<>();

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnalysisMode() {
        return analysisMode;
    }

    public void setAnalysisMode(String analysisMode) {
        this.analysisMode = analysisMode;
    }

    public List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public void setSelectedPaths(List<String> selectedPaths) {
        this.selectedPaths = selectedPaths == null ? new ArrayList<>() : selectedPaths;
    }

    public List<WorkspaceFileInput> getFiles() {
        return files;
    }

    public void setFiles(List<WorkspaceFileInput> files) {
        this.files = files == null ? new ArrayList<>() : files;
    }

    public static class WorkspaceFileInput {
        private String path;
        private String code;
        private String language;

        public WorkspaceFileInput() {
        }

        public WorkspaceFileInput(String path, String code, String language) {
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
