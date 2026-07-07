package com.yoonus.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceContextResponse {

    private Long projectId;
    private String projectSummary;
    private String analysisMode;
    private List<String> keyFiles = new ArrayList<>();
    private List<String> riskFlags = new ArrayList<>();
    private List<String> recommendedFocusAreas = new ArrayList<>();
    private List<String> recentActions = new ArrayList<>();
    private List<String> dependencyGraph = new ArrayList<>();
    private List<String> workspaceIndex = new ArrayList<>();
    private List<String> fileRelationships = new ArrayList<>();
    private String architectureVisualization;

    public WorkspaceContextResponse() {
    }

    public WorkspaceContextResponse(Long projectId, String projectSummary, String analysisMode, List<String> keyFiles, List<String> riskFlags, List<String> recommendedFocusAreas, List<String> recentActions) {
        this.projectId = projectId;
        this.projectSummary = projectSummary;
        this.analysisMode = analysisMode;
        this.keyFiles = keyFiles == null ? new ArrayList<>() : keyFiles;
        this.riskFlags = riskFlags == null ? new ArrayList<>() : riskFlags;
        this.recommendedFocusAreas = recommendedFocusAreas == null ? new ArrayList<>() : recommendedFocusAreas;
        this.recentActions = recentActions == null ? new ArrayList<>() : recentActions;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public String getAnalysisMode() {
        return analysisMode;
    }

    public void setAnalysisMode(String analysisMode) {
        this.analysisMode = analysisMode;
    }

    public List<String> getKeyFiles() {
        return keyFiles;
    }

    public void setKeyFiles(List<String> keyFiles) {
        this.keyFiles = keyFiles == null ? new ArrayList<>() : keyFiles;
    }

    public List<String> getRiskFlags() {
        return riskFlags;
    }

    public void setRiskFlags(List<String> riskFlags) {
        this.riskFlags = riskFlags == null ? new ArrayList<>() : riskFlags;
    }

    public List<String> getRecommendedFocusAreas() {
        return recommendedFocusAreas;
    }

    public void setRecommendedFocusAreas(List<String> recommendedFocusAreas) {
        this.recommendedFocusAreas = recommendedFocusAreas == null ? new ArrayList<>() : recommendedFocusAreas;
    }

    public List<String> getRecentActions() {
        return recentActions;
    }

    public void setRecentActions(List<String> recentActions) {
        this.recentActions = recentActions == null ? new ArrayList<>() : recentActions;
    }

    public List<String> getDependencyGraph() {
        return dependencyGraph;
    }

    public void setDependencyGraph(List<String> dependencyGraph) {
        this.dependencyGraph = dependencyGraph == null ? new ArrayList<>() : dependencyGraph;
    }

    public List<String> getWorkspaceIndex() {
        return workspaceIndex;
    }

    public void setWorkspaceIndex(List<String> workspaceIndex) {
        this.workspaceIndex = workspaceIndex == null ? new ArrayList<>() : workspaceIndex;
    }

    public List<String> getFileRelationships() {
        return fileRelationships;
    }

    public void setFileRelationships(List<String> fileRelationships) {
        this.fileRelationships = fileRelationships == null ? new ArrayList<>() : fileRelationships;
    }

    public String getArchitectureVisualization() {
        return architectureVisualization;
    }

    public void setArchitectureVisualization(String architectureVisualization) {
        this.architectureVisualization = architectureVisualization;
    }
}
