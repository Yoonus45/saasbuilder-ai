package com.yoonus.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiGenerationHistoryResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.entity.AiGenerationHistory;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.AiService;
import com.yoonus.backend.service.ProjectVersionService;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AiServiceImpl implements AiService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AiGenerationHistoryRepository generationHistoryRepository;
    private final AiCodeGenerator aiCodeGenerator;
    private final ProjectVersionService projectVersionService;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(ProjectRepository projectRepository,
                         UserRepository userRepository,
                         AiGenerationHistoryRepository generationHistoryRepository,
                         AiCodeGenerator aiCodeGenerator,
                         ProjectVersionService projectVersionService,
                         ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.generationHistoryRepository = generationHistoryRepository;
        this.aiCodeGenerator = aiCodeGenerator;
        this.projectVersionService = projectVersionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiGenerationResponse generateCode(String email, AiGenerationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        ProjectStatus status;
        String generatedCode;
        String message;

        try {
            generatedCode = aiCodeGenerator.generateCode(request.getPrompt(), request.getFramework());
            if (generatedCode == null || generatedCode.isBlank()) {
                throw new IllegalStateException("The AI provider returned empty content");
            }
            status = ProjectStatus.COMPLETED;
            message = "Code generated successfully";
        } catch (Exception ex) {
            generatedCode = buildFailurePayload(request.getPrompt(), request.getFramework(), ex.getMessage());
            status = ProjectStatus.FAILED;
            message = "AI generation failed: " + ex.getMessage();
        }

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setPrompt(request.getPrompt());
        project.setGeneratedCode(generatedCode);
        project.setFramework(request.getFramework());
        project.setStatus(status);
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        generationHistoryRepository.save(new AiGenerationHistory(user, request.getTitle(), request.getPrompt(), generatedCode, request.getFramework()));
        projectVersionService.createVersion(savedProject.getId(), message, request.getPrompt(), generatedCode, request.getFramework(), status);

        return new AiGenerationResponse(
                savedProject.getId(),
                savedProject.getTitle(),
                message,
                savedProject.getStatus(),
                savedProject.getGeneratedCode()
        );
    }

    @Override
    public AiReviewResponse reviewCode(String email, AiReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Review the supplied codebase for ")
                .append(request.getReviewType() == null ? "quality" : request.getReviewType())
                .append(" issues. Return compact JSON only with overallScore, summary, reviewType, suggestions (array of {id, severity, title, file, description, suggestedFix, line}).\n");
        promptBuilder.append("Files:\n");
        request.getFiles().forEach((file) -> promptBuilder.append(file.getPath()).append("\n").append(file.getCode()).append("\n---\n"));

        String rawPayload = aiCodeGenerator.reviewCode(promptBuilder.toString(), request.getReviewType());
        return parseReviewPayload(rawPayload, request.getReviewType(), user);
    }

    @Override
    public List<AiGenerationHistoryResponse> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return generationHistoryRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AiGenerationHistoryResponse mapToResponse(AiGenerationHistory history) {
        return new AiGenerationHistoryResponse(
                history.getId(),
                history.getTitle(),
                history.getPrompt(),
                history.getGeneratedCode(),
                history.getFramework(),
                history.getCreatedAt()
        );
    }

    private AiReviewResponse parseReviewPayload(String rawPayload, String reviewType, User user) {
        if (rawPayload == null || rawPayload.isBlank()) {
            return new AiReviewResponse(0, "No review output returned.", reviewType, new ArrayList<>());
        }

        try {
            JsonNode node = objectMapper.readTree(rawPayload);
            int overallScore = node.path("overallScore").asInt(70);
            String summary = node.path("summary").asText("Review completed.");
            String resolvedReviewType = node.path("reviewType").asText(reviewType);
            List<AiReviewResponse.ReviewSuggestion> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = node.path("suggestions");
            if (suggestionsNode.isArray()) {
                suggestions = objectMapper.convertValue(suggestionsNode, new TypeReference<List<AiReviewResponse.ReviewSuggestion>>() {});
            }
            return new AiReviewResponse(overallScore, summary, resolvedReviewType, suggestions);
        } catch (Exception ex) {
            return new AiReviewResponse(0, "Review parsing failed: " + ex.getMessage(), reviewType, new ArrayList<>());
        }
    }

    private String buildFailurePayload(String prompt, String framework, String errorMessage) {
        return "{\"summary\":\"AI generation failed\",\"framework\":\"" + escape(framework) + "\",\"prompt\":\"" + escape(prompt) + "\",\"error\":\"" + escape(errorMessage) + "\",\"files\":[]}";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
