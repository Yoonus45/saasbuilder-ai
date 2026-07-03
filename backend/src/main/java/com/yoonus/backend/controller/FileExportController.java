package com.yoonus.backend.controller;

import com.yoonus.backend.dto.FileExportResponse;
import com.yoonus.backend.service.FileExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class FileExportController {

    private final FileExportService fileExportService;

    public FileExportController(FileExportService fileExportService) {
        this.fileExportService = fileExportService;
    }

    @PostMapping("/{id}/export")
    public ResponseEntity<FileExportResponse> exportProject(
            @PathVariable Long id,
            Authentication authentication) {
        FileExportResponse response = fileExportService.exportProjectAsZip(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadProject(
            @PathVariable Long id,
            Authentication authentication) {
        byte[] zipData = fileExportService.exportProjectCode(id, authentication.getName());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "project-" + id + ".zip");
        headers.setContentLength(zipData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }
}
