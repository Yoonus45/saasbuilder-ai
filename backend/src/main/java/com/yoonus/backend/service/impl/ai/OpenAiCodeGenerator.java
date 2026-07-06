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
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiCodeGenerator implements AiCodeGenerator {

    private static final int MAX_RETRIES = 2;

    private final String apiKey;
    private final String model;
    private final String endpoint;
    private final int timeoutSeconds;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCodeGenerator(
            @Value("${openrouter.api-key:}") String apiKey,
            @Value("${openrouter.model:deepseek/deepseek-chat-v3-0324}") String model,
            @Value("${openrouter.endpoint:https://openrouter.ai/api/v1/chat/completions}") String endpoint,
            @Value("${openrouter.timeout.seconds:30}") int timeoutSeconds,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.endpoint = endpoint;
        this.timeoutSeconds = timeoutSeconds;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String generateCode(String prompt, String framework) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenRouter API key is not configured. Set OPENROUTER_API_KEY before generating code.");
        }

        Exception lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Map<String, Object> requestBody = new LinkedHashMap<>();
                requestBody.put("model", model);
                requestBody.put("temperature", 0.1);
                requestBody.put("max_tokens", 600);
                requestBody.put("response_format", Map.of("type", "json_object"));
                requestBody.put("messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are a concise coding assistant. Return compact JSON only with summary, framework, and files (array of {path, language, code})."
                        ),
                        Map.of(
                                "role", "user",
                                "content", "Create a tiny starter response for: " + prompt + " in " + framework + ". Return JSON only."
                        )
                ));

                String body = objectMapper.writeValueAsString(requestBody);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .timeout(Duration.ofSeconds(timeoutSeconds))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .header("HTTP-Referer", "https://localhost")
                        .header("X-Title", "SaaSBuilder AI")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 400) {
                    throw new IllegalStateException("OpenRouter API error " + response.statusCode() + ": " + response.body());
                }

                JsonNode root = objectMapper.readTree(response.body());
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                String content = extractContent(contentNode);
                if (content == null || content.isBlank()) {
                    throw new IllegalStateException("OpenRouter returned no usable content");
                }

                return normalizeGeneratedCode(content, prompt, framework);
            } catch (IllegalStateException ex) {
                lastException = ex;
            } catch (Exception ex) {
                lastException = new IllegalStateException("OpenRouter request failed: " + ex.getMessage(), ex);
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(500L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("OpenRouter request interrupted", interruptedException);
                }
            }
        }

        throw new IllegalStateException("OpenRouter request failed after retries", lastException);
    }

    @Override
    public String reviewCode(String reviewPrompt, String reviewType) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenRouter API key is not configured. Set OPENROUTER_API_KEY before generating code.");
        }

        Exception lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Map<String, Object> requestBody = new LinkedHashMap<>();
                requestBody.put("model", model);
                requestBody.put("temperature", 0.1);
                requestBody.put("max_tokens", 800);
                requestBody.put("response_format", Map.of("type", "json_object"));
                requestBody.put("messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are an expert code reviewer. Return compact JSON only with overallScore, summary, reviewType, and suggestions (array of {id, severity, title, file, description, suggestedFix, line})."
                        ),
                        Map.of(
                                "role", "user",
                                "content", "Review this code for " + (reviewType == null ? "quality" : reviewType) + " issues.\n" + reviewPrompt
                        )
                ));

                String body = objectMapper.writeValueAsString(requestBody);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .timeout(Duration.ofSeconds(timeoutSeconds))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .header("HTTP-Referer", "https://localhost")
                        .header("X-Title", "SaaSBuilder AI")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 400) {
                    throw new IllegalStateException("OpenRouter API error " + response.statusCode() + ": " + response.body());
                }

                JsonNode root = objectMapper.readTree(response.body());
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                String content = extractContent(contentNode);
                if (content == null || content.isBlank()) {
                    throw new IllegalStateException("OpenRouter returned no usable content");
                }

                return normalizeReviewPayload(content, reviewType);
            } catch (IllegalStateException ex) {
                lastException = ex;
            } catch (Exception ex) {
                lastException = new IllegalStateException("OpenRouter request failed: " + ex.getMessage(), ex);
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(500L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("OpenRouter request interrupted", interruptedException);
                }
            }
        }

        throw new IllegalStateException("OpenRouter request failed after retries", lastException);
    }

    private String extractContent(JsonNode contentNode) {
        if (contentNode.isTextual()) {
            return contentNode.asText();
        }
        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode item : contentNode) {
                if (item.has("text")) {
                    builder.append(item.get("text").asText());
                }
            }
            return builder.toString();
        }
        return null;
    }

    private String normalizeGeneratedCode(String content, String prompt, String framework) throws Exception {
        try {
            JsonNode parsed = objectMapper.readTree(content);
            if (parsed.isObject()) {
                return objectMapper.writeValueAsString(parsed);
            }
        } catch (Exception ignored) {
            // Fall through to a wrapped fallback structure below.
        }

        String extension = resolveExtension(framework);
        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("summary", "Generated starter implementation");
        fallback.put("framework", framework);
        fallback.put("prompt", prompt);
        fallback.put("files", List.of(Map.of(
                "path", "src/App." + extension,
                "language", extension,
                "code", content
        )));
        return objectMapper.writeValueAsString(fallback);
    }

    private String normalizeReviewPayload(String content, String reviewType) throws Exception {
        try {
            JsonNode parsed = objectMapper.readTree(content);
            if (parsed.isObject()) {
                if (!parsed.has("reviewType") && reviewType != null) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) parsed).put("reviewType", reviewType);
                }
                if (!parsed.has("overallScore")) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) parsed).put("overallScore", 75);
                }
                if (!parsed.has("suggestions")) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) parsed).putArray("suggestions");
                }
                return objectMapper.writeValueAsString(parsed);
            }
        } catch (Exception ignored) {
            // Fall through to a compact fallback payload below.
        }

        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("overallScore", 70);
        fallback.put("summary", "Review completed with a conservative fallback.");
        fallback.put("reviewType", reviewType == null ? "quality" : reviewType);
        fallback.put("suggestions", List.of());
        return objectMapper.writeValueAsString(fallback);
    }

    private String resolveExtension(String framework) {
        if (framework == null) {
            return "tsx";
        }
        String normalized = framework.toLowerCase();
        if (normalized.contains("react") || normalized.contains("next")) {
            return "tsx";
        }
        if (normalized.contains("vue")) {
            return "vue";
        }
        if (normalized.contains("angular")) {
            return "ts";
        }
        return "js";
    }
}
