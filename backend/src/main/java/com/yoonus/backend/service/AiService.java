package com.yoonus.backend.service;

import com.yoonus.backend.dto.AiGenerationHistoryResponse;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.dto.WorkspaceAnalysisRequest;
import com.yoonus.backend.dto.WorkspaceAnalysisResponse;
import com.yoonus.backend.dto.WorkspaceContextRequest;
import com.yoonus.backend.dto.WorkspaceContextResponse;

import java.util.List;

public interface AiService {

    AiGenerationResponse generateCode(String email, AiGenerationRequest request);

    AiReviewResponse reviewCode(String email, AiReviewRequest request);

    WorkspaceContextResponse buildWorkspaceContext(String email, WorkspaceContextRequest request);

    WorkspaceAnalysisResponse analyzeWorkspace(String email, WorkspaceAnalysisRequest request);

    List<AiGenerationHistoryResponse> getHistory(String email);
}
