package com.yoonus.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceAnalysisResponse {

    private Long projectId;
    private String summary;
    private String analysisMode;
    private List<String> findings = new ArrayList<>();
    private List<String> crossFileIssues = new ArrayList<>();
    private List<String> suggestedNextActions = new ArrayList<>();
    private List<String> proposedFileChanges = new ArrayList<>();
    private String riskLevel;

    public WorkspaceAnalysisResponse() {
    }

    public WorkspaceAnalysisResponse(Long projectId, String summary, String analysisMode, List<String> findings, List<String> crossFileIssues, List<String> suggestedNextActions, List<String> proposedFileChanges, String riskLevel) {
        this.projectId = projectId;
        this.summary = summary;
        this.analysisMode = analysisMode;
        this.findings = findings == null ? new ArrayList<>() : findings;
        this.crossFileIssues = crossFileIssues == null ? new ArrayList<>() : crossFileIssues;
        this.suggestedNextActions = suggestedNextActions == null ? new ArrayList<>() : suggestedNextActions;
        this.proposedFileChanges = proposedFileChanges == null ? new ArrayList<>() : proposedFileChanges;
        this.riskLevel = riskLevel;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAnalysisMode() {
        return analysisMode;
    }

    public void setAnalysisMode(String analysisMode) {
        this.analysisMode = analysisMode;
    }

    public List<String> getFindings() {
        return findings;
    }

    public void setFindings(List<String> findings) {
        this.findings = findings == null ? new ArrayList<>() : findings;
    }

    public List<String> getCrossFileIssues() {
        return crossFileIssues;
    }

    public void setCrossFileIssues(List<String> crossFileIssues) {
        this.crossFileIssues = crossFileIssues == null ? new ArrayList<>() : crossFileIssues;
    }

    public List<String> getSuggestedNextActions() {
        return suggestedNextActions;
    }

    public void setSuggestedNextActions(List<String> suggestedNextActions) {
        this.suggestedNextActions = suggestedNextActions == null ? new ArrayList<>() : suggestedNextActions;
    }

    public List<String> getProposedFileChanges() {
        return proposedFileChanges;
    }

    public void setProposedFileChanges(List<String> proposedFileChanges) {
        this.proposedFileChanges = proposedFileChanges == null ? new ArrayList<>() : proposedFileChanges;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
