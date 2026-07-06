package com.yoonus.backend.controller;

import com.yoonus.backend.dto.ProjectVersionResponse;
import com.yoonus.backend.service.ProjectVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/versions")
public class ProjectVersionController {

    private final ProjectVersionService projectVersionService;

    public ProjectVersionController(ProjectVersionService projectVersionService) {
        this.projectVersionService = projectVersionService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectVersionResponse>> listVersions(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectVersionService.listVersions(projectId));
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<ProjectVersionResponse> getVersion(@PathVariable Long projectId, @PathVariable Long versionId) {
        return ResponseEntity.ok(projectVersionService.getVersion(projectId, versionId));
    }

    @PostMapping("/restore")
    public ResponseEntity<ProjectVersionResponse> restoreVersion(@PathVariable Long projectId, @RequestParam Long versionId) {
        return ResponseEntity.ok(projectVersionService.restoreVersion(projectId, versionId));
    }
}
