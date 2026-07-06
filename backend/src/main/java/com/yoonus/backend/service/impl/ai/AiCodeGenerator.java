package com.yoonus.backend.service.impl.ai;

public interface AiCodeGenerator {
    String generateCode(String prompt, String framework);

    default String reviewCode(String reviewPrompt, String reviewType) {
        return generateCode(reviewPrompt, reviewType);
    }
}
