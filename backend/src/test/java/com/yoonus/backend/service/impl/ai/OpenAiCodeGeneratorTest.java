package com.yoonus.backend.service.impl.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenAiCodeGeneratorTest {

    @Test
    void generateCode_shouldThrowWhenApiKeyIsMissing() {
        OpenAiCodeGenerator generator = new OpenAiCodeGenerator(
                "",
                "gpt-4.1",
                "https://api.openai.com/v1/chat/completions",
                30,
                new ObjectMapper()
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> generator.generateCode("Create a landing page", "React"));

        assertTrue(exception.getMessage().contains("API key"));
    }
}
