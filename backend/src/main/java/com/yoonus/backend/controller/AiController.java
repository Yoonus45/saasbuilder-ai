package com.yoonus.backend.controller;

import com.yoonus.backend.dto.AiGenerationHistoryResponse;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.dto.WorkspaceAnalysisRequest;
import com.yoonus.backend.dto.WorkspaceAnalysisResponse;
import com.yoonus.backend.dto.WorkspaceContextRequest;
import com.yoonus.backend.dto.WorkspaceContextResponse;
import com.yoonus.backend.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AiGenerationResponse> generate(@Valid @RequestBody AiGenerationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        AiGenerationResponse response = aiService.generateCode(email, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/review")
    public ResponseEntity<AiReviewResponse> review(@Valid @RequestBody AiReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        AiReviewResponse response = aiService.reviewCode(email, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/workspace/context")
    public ResponseEntity<WorkspaceContextResponse> buildWorkspaceContext(@RequestBody WorkspaceContextRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        WorkspaceContextResponse response = aiService.buildWorkspaceContext(email, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/workspace/analyze")
    public ResponseEntity<WorkspaceAnalysisResponse> analyzeWorkspace(@RequestBody WorkspaceAnalysisRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        WorkspaceAnalysisResponse response = aiService.analyzeWorkspace(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AiGenerationHistoryResponse>> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<AiGenerationHistoryResponse> history = aiService.getHistory(email);
        return ResponseEntity.ok(history);
    }
}
