package com.yoonus.backend.service.impl.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiCodeGeneratorTest {

    @Test
    void generateCode_shouldReturnFallbackWhenApiKeyIsMissing() {
        OpenAiCodeGenerator generator = new OpenAiCodeGenerator(
                "",
                "gpt-4o-mini",
                "https://api.openai.com/v1/chat/completions",
                new ObjectMapper()
        );

        String code = generator.generateCode("Create a landing page", "React");

        assertTrue(code.contains("Generated locally"));
    }
}
