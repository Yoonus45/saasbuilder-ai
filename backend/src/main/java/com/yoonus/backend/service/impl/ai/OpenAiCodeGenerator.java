package com.yoonus.backend.service.impl.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class OpenAiCodeGenerator implements AiCodeGenerator {

    private final String apiKey;
    private final String model;
    private final String endpoint;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCodeGenerator(
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model,
            @Value("${openai.endpoint:https://api.openai.com/v1/chat/completions}") String endpoint,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.endpoint = endpoint;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String generateCode(String prompt, String framework) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackCode(prompt, framework);
        }

        try {
            String body = "{\"model\":\"" + model + "\",\"messages\":[{\"role\":\"system\",\"content\":\"You generate concise code snippets for the requested framework.\"},{\"role\":\"user\",\"content\":\"Generate a simple starter component for: " + prompt + " in " + framework + "\"}]}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return fallbackCode(prompt, framework);
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode message = root.path("choices").path(0).path("message").path("content");
            if (message.isTextual() && !message.asText().isBlank()) {
                return message.asText();
            }
        } catch (Exception ex) {
            return fallbackCode(prompt, framework);
        }

        return fallbackCode(prompt, framework);
    }

    private String fallbackCode(String prompt, String framework) {
        String normalizedFramework = framework == null ? "generic" : framework.toLowerCase();
        return "// Prompt: " + prompt + "\n"
                + "// Framework: " + normalizedFramework + "\n"
                + "export default function generatedComponent() {\n"
                + "  return 'Generated locally';\n"
                + "}";
    }
}
