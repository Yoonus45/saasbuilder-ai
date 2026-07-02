package com.yoonus.backend.service;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;

public interface AiService {

    AiGenerationResponse generateCode(String email, AiGenerationRequest request);
}
