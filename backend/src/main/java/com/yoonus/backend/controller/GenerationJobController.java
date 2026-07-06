package com.yoonus.backend.controller;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.GenerationJobResponse;
import com.yoonus.backend.service.GenerationJobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/jobs")
public class GenerationJobController {

    private final GenerationJobService generationJobService;

    public GenerationJobController(GenerationJobService generationJobService) {
        this.generationJobService = generationJobService;
    }

    @PostMapping
    public ResponseEntity<GenerationJobResponse> createJob(@Valid @RequestBody AiGenerationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(generationJobService.createJob(email, request));
    }

    @GetMapping
    public ResponseEntity<List<GenerationJobResponse>> listJobs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(generationJobService.getJobs(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenerationJobResponse> getJob(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(generationJobService.getJob(email, id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<GenerationJobResponse> cancelJob(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(generationJobService.cancelJob(email, id));
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<GenerationJobResponse> retryJob(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(generationJobService.retryJob(email, id));
    }
}
