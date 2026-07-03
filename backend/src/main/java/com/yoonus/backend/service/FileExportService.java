package com.yoonus.backend.service;

import com.yoonus.backend.dto.FileExportResponse;

public interface FileExportService {

    /**
     * Export project code to a ZIP file
     */
    FileExportResponse exportProjectAsZip(Long projectId, String email);

    /**
     * Export single code file
     */
    byte[] exportProjectCode(Long projectId, String email);
}
