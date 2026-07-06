package com.yoonus.backend.dto;

public class GeneratedFileDto {
    private String path;
    private String language;
    private String code;

    public GeneratedFileDto() {
    }

    public GeneratedFileDto(String path, String language, String code) {
        this.path = path;
        this.language = language;
        this.code = code;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
