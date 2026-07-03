package com.yoonus.backend.dto;

public class FileExportResponse {

    private String downloadUrl;
    private String fileName;
    private long fileSize;
    private String message;

    public FileExportResponse() {
    }

    public FileExportResponse(String downloadUrl, String fileName, long fileSize, String message) {
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.message = message;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
