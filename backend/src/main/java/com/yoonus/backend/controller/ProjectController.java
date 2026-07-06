package com.yoonus.backend.controller;

import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.CreateProjectRequest;
import com.yoonus.backend.dto.ProjectResponse;
import com.yoonus.backend.dto.UpdateProjectRequest;
import com.yoonus.backend.entity.GeneratedFile;
import com.yoonus.backend.service.GeneratedFileService;
import com.yoonus.backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final GeneratedFileService generatedFileService;
    private final ProjectService projectService;

    public ProjectController(GeneratedFileService generatedFileService, ProjectService projectService) {
        this.generatedFileService = generatedFileService;
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getAllProjects(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(projectService.getProject(id, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(projectService.createProject(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateProjectRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(projectService.updateProject(id, authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        projectService.deleteProject(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<TreeStructure> getFilesByProjectId(@PathVariable Long id) {
        List<GeneratedFile> files = generatedFileService.getFilesByProjectId(id);
        TreeStructure tree = buildFileTree(files);
        return ResponseEntity.ok(tree);
    }

    private TreeStructure buildFileTree(List<GeneratedFile> files) {
        // Implementation to convert list to tree structure
        // Group files by folder paths and create hierarchical structure
        return new TreeStructure(); // Placeholder - actual implementation needed
    }

    // TreeStructure class definition
    static class TreeStructure {
        private List<TreeNode> root;

        public TreeStructure() {
            this.root = new ArrayList<>();
        }

        public List<TreeNode> getRoot() {
            return root;
        }

        public void setRoot(List<TreeNode> root) {
            this.root = root;
        }

        static class TreeNode {
            private String path;
            private String language;
            private String code;
            private List<TreeNode> children;

            public TreeNode(String path, String language, String code) {
                this.path = path;
                this.language = language;
                this.code = code;
                this.children = new ArrayList<>();
            }

            // Getters and setters
        }
    }
}
