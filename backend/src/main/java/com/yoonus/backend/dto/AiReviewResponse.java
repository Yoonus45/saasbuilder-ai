package com.yoonus.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AiReviewResponse {

    private int overallScore;
    private String summary;
    private String reviewType;
    private List<ReviewSuggestion> suggestions = new ArrayList<>();

    public AiReviewResponse() {
    }

    public AiReviewResponse(int overallScore, String summary, String reviewType, List<ReviewSuggestion> suggestions) {
        this.overallScore = overallScore;
        this.summary = summary;
        this.reviewType = reviewType;
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public List<ReviewSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<ReviewSuggestion> suggestions) {
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public static class ReviewSuggestion {
        private String id;
        private String severity;
        private String title;
        private String file;
        private String description;
        private String suggestedFix;
        private String line;

        public ReviewSuggestion() {
        }

        public ReviewSuggestion(String id, String severity, String title, String file, String description, String suggestedFix, String line) {
            this.id = id;
            this.severity = severity;
            this.title = title;
            this.file = file;
            this.description = description;
            this.suggestedFix = suggestedFix;
            this.line = line;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSuggestedFix() {
            return suggestedFix;
        }

        public void setSuggestedFix(String suggestedFix) {
            this.suggestedFix = suggestedFix;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }
    }
}
