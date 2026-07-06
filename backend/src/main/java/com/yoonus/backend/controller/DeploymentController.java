package com.yoonus.backend.controller;

import com.yoonus.backend.entity.Deployment;
import com.yoonus.backend.service.DeploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deploy")
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping("/vercel")
    public ResponseEntity<Deployment> deployToVercel(@RequestBody Map<String, Object> payload) {
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        Deployment deployment = deploymentService.deployProject(currentUserEmail(), projectId, "Vercel");
        deploymentService.updateDeploymentStatus(deployment.getId(), "Building", null);
        deploymentService.updateDeploymentStatus(deployment.getId(), "Uploading", null);
        deploymentService.updateDeploymentStatus(deployment.getId(), "Ready", "https://example-vercel.app/" + deployment.getDeploymentId());
        return ResponseEntity.ok(deploymentService.getHistory(currentUserEmail()).get(0));
    }

    @PostMapping("/netlify")
    public ResponseEntity<Deployment> deployToNetlify(@RequestBody Map<String, Object> payload) {
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        Deployment deployment = deploymentService.deployProject(currentUserEmail(), projectId, "Netlify");
        deploymentService.updateDeploymentStatus(deployment.getId(), "Building", null);
        deploymentService.updateDeploymentStatus(deployment.getId(), "Uploading", null);
        deploymentService.updateDeploymentStatus(deployment.getId(), "Ready", "https://example-netlify.app/" + deployment.getDeploymentId());
        return ResponseEntity.ok(deploymentService.getHistory(currentUserEmail()).get(0));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Deployment>> history() {
        return ResponseEntity.ok(deploymentService.getHistory(currentUserEmail()));
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
