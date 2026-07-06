package com.yoonus.backend.service;

import com.yoonus.backend.dto.AiGenerationHistoryResponse;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;

import java.util.List;

public interface AiService {

    AiGenerationResponse generateCode(String email, AiGenerationRequest request);

    AiReviewResponse reviewCode(String email, AiReviewRequest request);

    List<AiGenerationHistoryResponse> getHistory(String email);
}
