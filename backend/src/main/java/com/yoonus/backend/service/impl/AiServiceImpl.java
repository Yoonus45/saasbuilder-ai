package com.yoonus.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiGenerationHistoryResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.dto.WorkspaceAnalysisRequest;
import com.yoonus.backend.dto.WorkspaceAnalysisResponse;
import com.yoonus.backend.dto.WorkspaceContextRequest;
import com.yoonus.backend.dto.WorkspaceContextResponse;
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
        if (request.getFileName() != null && !request.getFileName().isBlank()) {
            String reviewIntent = request.getReviewType() == null ? "quality" : request.getReviewType();
            promptBuilder.append("Review the supplied file ")
                    .append(request.getFileName())
                    .append(" for ")
                    .append(reviewIntent)
                    .append(" issues. Return compact JSON only with overallScore, summary, reviewType, severity, confidenceScore, estimatedImprovement, issues (array of issue strings), suggestedFixes (array of strings), optimizedCode, and suggestions (array of {id, severity, title, file, description, suggestedFix, line, lineNumber, confidenceScore}).\n");
            if ("bug-fix".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Focus on concrete bugs, runtime errors, null handling, and broken logic. If a bug is identified, include a short explanation, a precise suggested fix, and an approximate line number when possible.\n");
            } else if ("refactor".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Focus on refactoring opportunities such as extracting methods, simplifying conditionals, removing duplicate code, improving readability, performance, and maintainability. If a refactoring is recommended, include an explanation, a precise suggestion, and an approximate line number when possible.\n");
            } else if ("test-generator".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Generate unit tests for the supplied file. Return compact JSON only with summary, reviewType, frameworkUsed, estimatedCoverage, edgeCasesCovered (array of strings), generatedTestCode, and optional suggestions. The generated test code should be ready to paste into a test file.\n");
            } else if ("documentation-generator".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Generate documentation for the supplied file. Return compact JSON only with summary, reviewType, documentationType, generatedDocumentation, suggestedInsertionPoints (array of strings), and optional suggestions. The documentation should be ready to insert into source or README files.\n");
            } else if ("explain".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Explain the supplied file in a beginner-friendly and advanced way. Return compact JSON only with summary, reviewType, purpose, algorithm, complexity, importantFunctions (array of strings), dependencies (array of strings), potentialImprovements (array of strings), beginnerExplanation, advancedExplanation, and optional suggestions.\n");
            } else if ("architecture".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and produce a high-level software architecture summary. Return compact JSON only with summary, reviewType, architectureSummary, applicationLayers (array of strings), frontendStructure (array of strings), backendStructure (array of strings), databaseEntities (array of strings), serviceDependencies (array of strings), apiFlow (array of strings), componentRelationships (array of strings), suggestedImprovements (array of strings), scalabilityRecommendations (array of strings), securityRecommendations (array of strings), and optional suggestions. Focus only on the current project and keep it high level.\n");
            } else if ("security".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and produce a practical security review for the current project. Return compact JSON only with summary, reviewType, securitySummary, vulnerabilities (array of strings), owaspTop10 (array of strings), authenticationIssues (array of strings), authorizationIssues (array of strings), jwtRecommendations (array of strings), csrfRecommendations (array of strings), xssRecommendations (array of strings), sqlInjectionRisks (array of strings), secretsFound (array of strings), dependencyRisks (array of strings), insecureConfigurations (array of strings), encryptionRecommendations (array of strings), inputValidationRecommendations (array of strings), securityHeaders (array of strings), rateLimitingRecommendations (array of strings), loggingAuditRecommendations (array of strings), complianceRecommendations (array of strings), generatedSecurityChecklist, generatedThreatModel, generatedSecurityMarkdown, and optional suggestions. Keep the output practical, include markdown in the text fields, and format any lists as JSON arrays.\n");
            } else if ("database".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and design or improve the database architecture for the current project. Return compact JSON only with summary, reviewType, databaseSummary, entities (array of strings), relationships (array of strings), primaryKeys (array of strings), foreignKeys (array of strings), indexes (array of strings), constraints (array of strings), normalizationSuggestions (array of strings), missingTables (array of strings), migrationSuggestions (array of strings), postgresRecommendations (array of strings), generatedSqlSchema, generatedMermaidERDiagram, and optional suggestions. Focus only on the current project and keep the output practical for PostgreSQL.\n");
            } else if ("api".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and design or improve the API surface for the current project. Return compact JSON only with summary, reviewType, apiSummary, resources (array of strings), endpoints (array of strings), requestDtos (array of strings), responseDtos (array of strings), validationRules (array of strings), authenticationRequirements (array of strings), authorizationRequirements (array of strings), httpStatusCodes (array of strings), errorResponses (array of strings), openApiSpec, curlExamples (array of strings), postmanExamples (array of strings), improvementSuggestions (array of strings), and optional suggestions. Focus only on the current project and keep the output practical for REST APIs.\n");
            } else if ("system".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and produce a complete high-level software system design for the current project. Return compact JSON only with summary, reviewType, systemSummary, projectOverview, frontendArchitecture (array of strings), backendArchitecture (array of strings), databaseArchitecture (array of strings), apiArchitecture (array of strings), serviceCommunication (array of strings), moduleDependencies (array of strings), deploymentArchitecture (array of strings), scalabilityRecommendations (array of strings), performanceRecommendations (array of strings), cachingStrategy, loadBalancingStrategy, securityArchitecture (array of strings), authenticationFlow (array of strings), authorizationFlow (array of strings), monitoringRecommendations (array of strings), loggingRecommendations (array of strings), disasterRecoveryRecommendations (array of strings), technologyRecommendations (array of strings), futureImprovements (array of strings), generatedSystemDesignMarkdown, generatedMermaidFlowDiagram, and optional suggestions. Focus only on the current project and keep the output practical and high level.\n");
            } else if ("devops".equalsIgnoreCase(reviewIntent)) {
                promptBuilder.append("Analyze the supplied project context and produce deployment guidance for the current project. Return compact JSON only with summary, reviewType, deploymentSummary, dockerRecommendations (array of strings), dockerfile, dockerCompose, kubernetesManifest, githubActionsWorkflow, environmentVariables (array of strings), secretsRequired (array of strings), deploymentSteps (array of strings), ciCdFlow (array of strings), monitoringRecommendations (array of strings), loggingRecommendations (array of strings), scalingRecommendations (array of strings), backupStrategy, rollbackStrategy, cloudRecommendations (array of strings), estimatedDeploymentCost, generatedMarkdown, and optional suggestions. Keep the output practical and ready to use in CI/CD and container deployment workflows.\n");
            }
            if (request.getRefactoringType() != null && !request.getRefactoringType().isBlank()) {
                promptBuilder.append("Requested refactoring type: ").append(request.getRefactoringType()).append("\n");
            }
            if (request.getTestFramework() != null && !request.getTestFramework().isBlank()) {
                promptBuilder.append("Requested test framework: ").append(request.getTestFramework()).append("\n");
            }
            if (request.getDocumentationType() != null && !request.getDocumentationType().isBlank()) {
                promptBuilder.append("Requested documentation type: ").append(request.getDocumentationType()).append("\n");
            }
            promptBuilder.append("File: ").append(request.getFileName()).append("\n");
            promptBuilder.append(request.getFileContent() == null ? "" : request.getFileContent()).append("\n---\n");
        } else {
            promptBuilder.append("Review the supplied codebase for ")
                    .append(request.getReviewType() == null ? "quality" : request.getReviewType())
                    .append(" issues. Return compact JSON only with overallScore, summary, reviewType, severity, confidenceScore, estimatedImprovement, issues (array of issue strings), suggestedFixes (array of strings), optimizedCode, and suggestions (array of {id, severity, title, file, description, suggestedFix, line}).\n");
            promptBuilder.append("Files:\n");
            request.getFiles().forEach((file) -> promptBuilder.append(file.getPath()).append("\n").append(file.getCode()).append("\n---\n"));
        }

        String rawPayload = aiCodeGenerator.reviewCode(promptBuilder.toString(), request.getReviewType());
        return parseReviewPayload(rawPayload, request.getReviewType(), user);
    }

    @Override
    public WorkspaceContextResponse buildWorkspaceContext(String email, WorkspaceContextRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Analyze the provided workspace files and return a JSON object representing the workspace context. ");
        promptBuilder.append("Return compact JSON only with projectSummary, keyFiles (array of strings), riskFlags (array of strings), recommendedFocusAreas (array of strings), recentActions (array of strings), dependencyGraph (array of strings), workspaceIndex (array of strings), fileRelationships (array of strings), and architectureVisualization (string with markdown/mermaid).\n");
        promptBuilder.append("Files:\n");
        if (request != null && request.getFiles() != null) {
            request.getFiles().forEach((file) -> {
                if (file != null && file.getPath() != null) {
                    promptBuilder.append(file.getPath()).append("\n").append(file.getCode() == null ? "" : file.getCode()).append("\n---\n");
                }
            });
        }

        String rawPayload = aiCodeGenerator.reviewCode(promptBuilder.toString(), "workspace-context");
        return parseWorkspaceContextPayload(rawPayload, request);
    }

    @Override
    public WorkspaceAnalysisResponse analyzeWorkspace(String email, WorkspaceAnalysisRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Perform a project-wide analysis on the provided workspace files. ");
        promptBuilder.append("Return compact JSON only with summary, projectSummary, riskLevel, findings (array of strings), crossFileIssues (array of strings), suggestedNextActions (array of strings), proposedFileChanges (array of strings), dependencyGraph (array of strings), workspaceIndex (array of strings), fileRelationships (array of strings), and architectureVisualization (string with markdown/mermaid).\n");
        if (request != null && request.getPrompt() != null && !request.getPrompt().isBlank()) {
            promptBuilder.append("User requested focus: ").append(request.getPrompt()).append("\n");
        }
        promptBuilder.append("Files:\n");
        if (request != null && request.getFiles() != null) {
            request.getFiles().forEach((file) -> {
                if (file != null && file.getPath() != null) {
                    promptBuilder.append(file.getPath()).append("\n").append(file.getCode() == null ? "" : file.getCode()).append("\n---\n");
                }
            });
        }

        String rawPayload = aiCodeGenerator.reviewCode(promptBuilder.toString(), "workspace-analysis");
        return parseWorkspaceAnalysisPayload(rawPayload, request);
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

    private WorkspaceContextResponse parseWorkspaceContextPayload(String rawPayload, WorkspaceContextRequest request) {
        WorkspaceContextResponse response = new WorkspaceContextResponse();
        response.setProjectId(request != null ? request.getProjectId() : null);
        response.setAnalysisMode(request != null ? request.getAnalysisMode() : null);

        if (rawPayload == null || rawPayload.isBlank()) {
            response.setProjectSummary("No workspace context returned.");
            return response;
        }

        try {
            String normalizedPayload = normalizeJsonPayload(rawPayload);
            JsonNode node;
            try {
                node = objectMapper.readTree(normalizedPayload);
            } catch (Exception parseException) {
                node = objectMapper.readTree(normalizedPayload.replace("\n", "\\n"));
            }
            response.setProjectSummary(node.path("projectSummary").asText("Workspace context generated."));
            response.setKeyFiles(parseStringArray(node.path("keyFiles")));
            response.setRiskFlags(parseStringArray(node.path("riskFlags")));
            response.setRecommendedFocusAreas(parseStringArray(node.path("recommendedFocusAreas")));
            response.setRecentActions(parseStringArray(node.path("recentActions")));
            response.setDependencyGraph(parseStringArray(node.path("dependencyGraph")));
            response.setWorkspaceIndex(parseStringArray(node.path("workspaceIndex")));
            response.setFileRelationships(parseStringArray(node.path("fileRelationships")));
            response.setArchitectureVisualization(node.path("architectureVisualization").asText(""));
        } catch (Exception ex) {
            response.setProjectSummary("Workspace context parsing failed: " + ex.getMessage());
        }
        return response;
    }

    private WorkspaceAnalysisResponse parseWorkspaceAnalysisPayload(String rawPayload, WorkspaceAnalysisRequest request) {
        WorkspaceAnalysisResponse response = new WorkspaceAnalysisResponse();
        response.setProjectId(request != null ? request.getProjectId() : null);
        response.setAnalysisMode(request != null ? request.getAnalysisMode() : null);

        if (rawPayload == null || rawPayload.isBlank()) {
            response.setSummary("No analysis returned.");
            return response;
        }

        try {
            String normalizedPayload = normalizeJsonPayload(rawPayload);
            JsonNode node;
            try {
                node = objectMapper.readTree(normalizedPayload);
            } catch (Exception parseException) {
                node = objectMapper.readTree(normalizedPayload.replace("\n", "\\n"));
            }
            response.setSummary(node.path("summary").asText("Project-wide analysis completed."));
            response.setProjectSummary(node.path("projectSummary").asText(""));
            response.setRiskLevel(node.path("riskLevel").asText("low"));
            response.setFindings(parseStringArray(node.path("findings")));
            response.setCrossFileIssues(parseStringArray(node.path("crossFileIssues")));
            response.setSuggestedNextActions(parseStringArray(node.path("suggestedNextActions")));
            response.setProposedFileChanges(parseStringArray(node.path("proposedFileChanges")));
            response.setDependencyGraph(parseStringArray(node.path("dependencyGraph")));
            response.setWorkspaceIndex(parseStringArray(node.path("workspaceIndex")));
            response.setFileRelationships(parseStringArray(node.path("fileRelationships")));
            response.setArchitectureVisualization(node.path("architectureVisualization").asText(""));
        } catch (Exception ex) {
            response.setSummary("Analysis parsing failed: " + ex.getMessage());
        }
        return response;
    }

    private AiReviewResponse parseReviewPayload(String rawPayload, String reviewType, User user) {
        if (rawPayload == null || rawPayload.isBlank()) {
            return new AiReviewResponse(0, "No review output returned.", reviewType, new ArrayList<>());
        }

        try {
            String normalizedPayload = normalizeJsonPayload(rawPayload);
            JsonNode node;
            try {
                node = objectMapper.readTree(normalizedPayload);
            } catch (Exception parseException) {
                node = objectMapper.readTree(normalizedPayload.replace("\n", "\\n"));
            }
            int overallScore = node.path("overallScore").asInt(70);
            String summary = node.path("summary").asText("Review completed.");
            String resolvedReviewType = node.path("reviewType").asText(reviewType);
            String severity = node.path("severity").asText("medium");
            Double confidenceScore = node.has("confidenceScore") && !node.path("confidenceScore").isMissingNode() ? node.path("confidenceScore").asDouble(0.0) : null;
            Integer estimatedImprovement = node.has("estimatedImprovement") && !node.path("estimatedImprovement").isMissingNode() ? node.path("estimatedImprovement").asInt(0) : null;
            String optimizedCode = node.path("optimizedCode").asText("");
            String generatedTestCode = node.path("generatedTestCode").asText("");
            String testSummary = node.path("testSummary").asText("");
            String frameworkUsed = node.path("frameworkUsed").asText("");
            Integer estimatedCoverage = node.has("estimatedCoverage") && !node.path("estimatedCoverage").isMissingNode() ? node.path("estimatedCoverage").asInt(0) : null;
            List<String> edgeCasesCovered = new ArrayList<>();
            JsonNode edgeCasesNode = node.path("edgeCasesCovered");
            if (edgeCasesNode.isArray()) {
                for (JsonNode edgeCaseNode : edgeCasesNode) {
                    if (edgeCaseNode.isTextual()) {
                        edgeCasesCovered.add(edgeCaseNode.asText());
                    }
                }
            }
            String generatedDocumentation = node.path("generatedDocumentation").asText("");
            String documentationType = node.path("documentationType").asText("");
            List<String> suggestedInsertionPoints = new ArrayList<>();
            JsonNode insertionPointsNode = node.path("suggestedInsertionPoints");
            if (insertionPointsNode.isArray()) {
                for (JsonNode insertionPointNode : insertionPointsNode) {
                    if (insertionPointNode.isTextual()) {
                        suggestedInsertionPoints.add(insertionPointNode.asText());
                    }
                }
            }
            String purpose = node.path("purpose").asText("");
            String algorithm = node.path("algorithm").asText("");
            String complexity = node.path("complexity").asText("");
            List<String> importantFunctions = new ArrayList<>();
            JsonNode importantFunctionsNode = node.path("importantFunctions");
            if (importantFunctionsNode.isArray()) {
                for (JsonNode importantFunctionNode : importantFunctionsNode) {
                    if (importantFunctionNode.isTextual()) {
                        importantFunctions.add(importantFunctionNode.asText());
                    }
                }
            }
            List<String> dependencies = new ArrayList<>();
            JsonNode dependenciesNode = node.path("dependencies");
            if (dependenciesNode.isArray()) {
                for (JsonNode dependencyNode : dependenciesNode) {
                    if (dependencyNode.isTextual()) {
                        dependencies.add(dependencyNode.asText());
                    }
                }
            }
            List<String> potentialImprovements = new ArrayList<>();
            JsonNode improvementsNode = node.path("potentialImprovements");
            if (improvementsNode.isArray()) {
                for (JsonNode improvementNode : improvementsNode) {
                    if (improvementNode.isTextual()) {
                        potentialImprovements.add(improvementNode.asText());
                    }
                }
            }
            String beginnerExplanation = node.path("beginnerExplanation").asText("");
            String advancedExplanation = node.path("advancedExplanation").asText("");
            String architectureSummary = node.path("architectureSummary").asText("");
            String securitySummary = node.path("securitySummary").asText("");
            List<String> vulnerabilities = parseStringArray(node.path("vulnerabilities"));
            List<String> owaspTop10 = parseStringArray(node.path("owaspTop10"));
            List<String> authenticationIssues = parseStringArray(node.path("authenticationIssues"));
            List<String> authorizationIssues = parseStringArray(node.path("authorizationIssues"));
            List<String> jwtRecommendations = parseStringArray(node.path("jwtRecommendations"));
            List<String> csrfRecommendations = parseStringArray(node.path("csrfRecommendations"));
            List<String> xssRecommendations = parseStringArray(node.path("xssRecommendations"));
            List<String> sqlInjectionRisks = parseStringArray(node.path("sqlInjectionRisks"));
            List<String> secretsFound = parseStringArray(node.path("secretsFound"));
            List<String> dependencyRisks = parseStringArray(node.path("dependencyRisks"));
            List<String> insecureConfigurations = parseStringArray(node.path("insecureConfigurations"));
            List<String> encryptionRecommendations = parseStringArray(node.path("encryptionRecommendations"));
            List<String> inputValidationRecommendations = parseStringArray(node.path("inputValidationRecommendations"));
            List<String> securityHeaders = parseStringArray(node.path("securityHeaders"));
            List<String> rateLimitingRecommendations = parseStringArray(node.path("rateLimitingRecommendations"));
            List<String> loggingAuditRecommendations = parseStringArray(node.path("loggingAuditRecommendations"));
            List<String> complianceRecommendations = parseStringArray(node.path("complianceRecommendations"));
            String generatedSecurityChecklist = node.path("generatedSecurityChecklist").asText("");
            String generatedThreatModel = node.path("generatedThreatModel").asText("");
            String generatedSecurityMarkdown = node.path("generatedSecurityMarkdown").asText("");
            String databaseSummary = node.path("databaseSummary").asText("");
            String apiSummary = node.path("apiSummary").asText("");
            List<String> applicationLayers = parseStringArray(node.path("applicationLayers"));
            List<String> frontendStructure = parseStringArray(node.path("frontendStructure"));
            List<String> backendStructure = parseStringArray(node.path("backendStructure"));
            List<String> databaseEntities = parseStringArray(node.path("databaseEntities"));
            List<String> serviceDependencies = parseStringArray(node.path("serviceDependencies"));
            List<String> apiFlow = parseStringArray(node.path("apiFlow"));
            List<String> componentRelationships = parseStringArray(node.path("componentRelationships"));
            List<String> suggestedImprovements = parseStringArray(node.path("suggestedImprovements"));
            List<String> architectureScalabilityRecommendations = parseStringArray(node.path("scalabilityRecommendations"));
            List<String> securityRecommendations = parseStringArray(node.path("securityRecommendations"));
            List<String> entities = parseStringArray(node.path("entities"));
            List<String> relationships = parseStringArray(node.path("relationships"));
            List<String> primaryKeys = parseStringArray(node.path("primaryKeys"));
            List<String> foreignKeys = parseStringArray(node.path("foreignKeys"));
            List<String> indexes = parseStringArray(node.path("indexes"));
            List<String> constraints = parseStringArray(node.path("constraints"));
            List<String> normalizationSuggestions = parseStringArray(node.path("normalizationSuggestions"));
            List<String> missingTables = parseStringArray(node.path("missingTables"));
            List<String> migrationSuggestions = parseStringArray(node.path("migrationSuggestions"));
            List<String> postgresRecommendations = parseStringArray(node.path("postgresRecommendations"));
            String generatedSqlSchema = node.path("generatedSqlSchema").asText("");
            String generatedMermaidERDiagram = node.path("generatedMermaidERDiagram").asText("");
            List<String> resources = parseStringArray(node.path("resources"));
            List<String> endpoints = parseStringArray(node.path("endpoints"));
            List<String> requestDtos = parseStringArray(node.path("requestDtos"));
            List<String> responseDtos = parseStringArray(node.path("responseDtos"));
            List<String> validationRules = parseStringArray(node.path("validationRules"));
            List<String> authenticationRequirements = parseStringArray(node.path("authenticationRequirements"));
            List<String> authorizationRequirements = parseStringArray(node.path("authorizationRequirements"));
            List<String> httpStatusCodes = parseStringArray(node.path("httpStatusCodes"));
            List<String> errorResponses = parseStringArray(node.path("errorResponses"));
            String openApiSpec = node.path("openApiSpec").asText("");
            List<String> curlExamples = parseStringArray(node.path("curlExamples"));
            List<String> postmanExamples = parseStringArray(node.path("postmanExamples"));
            List<String> improvementSuggestions = parseStringArray(node.path("improvementSuggestions"));
            String systemSummary = node.path("systemSummary").asText("");
            String projectOverview = node.path("projectOverview").asText("");
            String deploymentSummary = node.path("deploymentSummary").asText("");
            List<String> dockerRecommendations = parseStringArray(node.path("dockerRecommendations"));
            String dockerfile = node.path("dockerfile").asText("");
            String dockerCompose = node.path("dockerCompose").asText("");
            String kubernetesManifest = node.path("kubernetesManifest").asText("");
            String githubActionsWorkflow = node.path("githubActionsWorkflow").asText("");
            List<String> environmentVariables = parseStringArray(node.path("environmentVariables"));
            List<String> secretsRequired = parseStringArray(node.path("secretsRequired"));
            List<String> deploymentSteps = parseStringArray(node.path("deploymentSteps"));
            List<String> ciCdFlow = parseStringArray(node.path("ciCdFlow"));
            List<String> deploymentMonitoringRecommendations = parseStringArray(node.path("monitoringRecommendations"));
            List<String> deploymentLoggingRecommendations = parseStringArray(node.path("loggingRecommendations"));
            List<String> deploymentScalingRecommendations = parseStringArray(node.path("scalingRecommendations"));
            String backupStrategy = node.path("backupStrategy").asText("");
            String rollbackStrategy = node.path("rollbackStrategy").asText("");
            List<String> cloudRecommendations = parseStringArray(node.path("cloudRecommendations"));
            String estimatedDeploymentCost = node.path("estimatedDeploymentCost").asText("");
            String generatedMarkdown = node.path("generatedMarkdown").asText("");
            List<String> frontendArchitecture = parseStringArray(node.path("frontendArchitecture"));
            List<String> backendArchitecture = parseStringArray(node.path("backendArchitecture"));
            List<String> databaseArchitecture = parseStringArray(node.path("databaseArchitecture"));
            List<String> apiArchitecture = parseStringArray(node.path("apiArchitecture"));
            List<String> serviceCommunication = parseStringArray(node.path("serviceCommunication"));
            List<String> moduleDependencies = parseStringArray(node.path("moduleDependencies"));
            List<String> deploymentArchitecture = parseStringArray(node.path("deploymentArchitecture"));
            List<String> scalabilityRecommendations = parseStringArray(node.path("scalabilityRecommendations"));
            List<String> performanceRecommendations = parseStringArray(node.path("performanceRecommendations"));
            String cachingStrategy = node.path("cachingStrategy").asText("");
            String loadBalancingStrategy = node.path("loadBalancingStrategy").asText("");
            List<String> securityArchitecture = parseStringArray(node.path("securityArchitecture"));
            List<String> authenticationFlow = parseStringArray(node.path("authenticationFlow"));
            List<String> authorizationFlow = parseStringArray(node.path("authorizationFlow"));
            List<String> monitoringRecommendations = parseStringArray(node.path("monitoringRecommendations"));
            List<String> loggingRecommendations = parseStringArray(node.path("loggingRecommendations"));
            List<String> disasterRecoveryRecommendations = parseStringArray(node.path("disasterRecoveryRecommendations"));
            List<String> technologyRecommendations = parseStringArray(node.path("technologyRecommendations"));
            List<String> futureImprovements = parseStringArray(node.path("futureImprovements"));
            String generatedSystemDesignMarkdown = node.path("generatedSystemDesignMarkdown").asText("");
            String generatedMermaidFlowDiagram = node.path("generatedMermaidFlowDiagram").asText("");
            List<String> issues = new ArrayList<>();
            JsonNode issuesNode = node.path("issues");
            if (issuesNode.isArray()) {
                for (JsonNode issueNode : issuesNode) {
                    if (issueNode.isTextual()) {
                        issues.add(issueNode.asText());
                    } else if (issueNode.isObject()) {
                        issues.add(issueNode.path("description").asText(issueNode.path("explanation").asText("")));
                    }
                }
            }
            List<String> suggestedFixes = new ArrayList<>();
            JsonNode suggestedFixesNode = node.path("suggestedFixes");
            if (suggestedFixesNode.isArray()) {
                for (JsonNode fixNode : suggestedFixesNode) {
                    if (fixNode.isTextual()) {
                        suggestedFixes.add(fixNode.asText());
                    }
                }
            }
            List<AiReviewResponse.ReviewSuggestion> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = node.path("suggestions");
            if (suggestionsNode.isArray()) {
                suggestions = objectMapper.convertValue(suggestionsNode, new TypeReference<List<AiReviewResponse.ReviewSuggestion>>() {});
            }
            if (suggestions.isEmpty() && !issues.isEmpty()) {
                for (int index = 0; index < issues.size(); index++) {
                    String issue = issues.get(index);
                    String suggestedFix = index < suggestedFixes.size() ? suggestedFixes.get(index) : "";
                    suggestions.add(new AiReviewResponse.ReviewSuggestion(
                            String.valueOf(index + 1),
                            severity,
                            issue.length() > 40 ? issue.substring(0, 40) : issue,
                            "current-file",
                            issue,
                            suggestedFix,
                            "",
                            "",
                            confidenceScore
                    ));
                }
            }
            AiReviewResponse response = new AiReviewResponse(overallScore, summary, resolvedReviewType, suggestions);
            response.setSeverity(severity);
            response.setConfidenceScore(confidenceScore);
            response.setEstimatedImprovement(estimatedImprovement);
            response.setIssues(issues);
            response.setSuggestedFixes(suggestedFixes);
            response.setOptimizedCode(optimizedCode);
            response.setGeneratedTestCode(generatedTestCode);
            response.setTestSummary(testSummary);
            response.setFrameworkUsed(frameworkUsed);
            response.setEstimatedCoverage(estimatedCoverage);
            response.setEdgeCasesCovered(edgeCasesCovered);
            response.setGeneratedDocumentation(generatedDocumentation);
            response.setDocumentationType(documentationType);
            response.setSuggestedInsertionPoints(suggestedInsertionPoints);
            response.setPurpose(purpose);
            response.setAlgorithm(algorithm);
            response.setComplexity(complexity);
            response.setImportantFunctions(importantFunctions);
            response.setDependencies(dependencies);
            response.setPotentialImprovements(potentialImprovements);
            response.setBeginnerExplanation(beginnerExplanation);
            response.setAdvancedExplanation(advancedExplanation);
            response.setArchitectureSummary(architectureSummary);
            response.setApplicationLayers(applicationLayers);
            response.setFrontendStructure(frontendStructure);
            response.setBackendStructure(backendStructure);
            response.setDatabaseEntities(databaseEntities);
            response.setServiceDependencies(serviceDependencies);
            response.setApiFlow(apiFlow);
            response.setComponentRelationships(componentRelationships);
            response.setSuggestedImprovements(suggestedImprovements);
            response.setScalabilityRecommendations(architectureScalabilityRecommendations);
            response.setSecurityRecommendations(securityRecommendations);
            response.setSecuritySummary(securitySummary);
            response.setVulnerabilities(vulnerabilities);
            response.setOwaspTop10(owaspTop10);
            response.setAuthenticationIssues(authenticationIssues);
            response.setAuthorizationIssues(authorizationIssues);
            response.setJwtRecommendations(jwtRecommendations);
            response.setCsrfRecommendations(csrfRecommendations);
            response.setXssRecommendations(xssRecommendations);
            response.setSqlInjectionRisks(sqlInjectionRisks);
            response.setSecretsFound(secretsFound);
            response.setDependencyRisks(dependencyRisks);
            response.setInsecureConfigurations(insecureConfigurations);
            response.setEncryptionRecommendations(encryptionRecommendations);
            response.setInputValidationRecommendations(inputValidationRecommendations);
            response.setSecurityHeaders(securityHeaders);
            response.setRateLimitingRecommendations(rateLimitingRecommendations);
            response.setLoggingAuditRecommendations(loggingAuditRecommendations);
            response.setComplianceRecommendations(complianceRecommendations);
            response.setGeneratedSecurityChecklist(generatedSecurityChecklist);
            response.setGeneratedThreatModel(generatedThreatModel);
            response.setGeneratedSecurityMarkdown(generatedSecurityMarkdown);
            response.setDatabaseSummary(databaseSummary);
            response.setEntities(entities);
            response.setRelationships(relationships);
            response.setPrimaryKeys(primaryKeys);
            response.setForeignKeys(foreignKeys);
            response.setIndexes(indexes);
            response.setConstraints(constraints);
            response.setNormalizationSuggestions(normalizationSuggestions);
            response.setMissingTables(missingTables);
            response.setMigrationSuggestions(migrationSuggestions);
            response.setPostgresRecommendations(postgresRecommendations);
            response.setGeneratedSqlSchema(generatedSqlSchema);
            response.setGeneratedMermaidERDiagram(generatedMermaidERDiagram);
            response.setApiSummary(apiSummary);
            response.setResources(resources);
            response.setEndpoints(endpoints);
            response.setRequestDtos(requestDtos);
            response.setResponseDtos(responseDtos);
            response.setValidationRules(validationRules);
            response.setAuthenticationRequirements(authenticationRequirements);
            response.setAuthorizationRequirements(authorizationRequirements);
            response.setHttpStatusCodes(httpStatusCodes);
            response.setErrorResponses(errorResponses);
            response.setOpenApiSpec(openApiSpec);
            response.setCurlExamples(curlExamples);
            response.setPostmanExamples(postmanExamples);
            response.setImprovementSuggestions(improvementSuggestions);
            response.setSystemSummary(systemSummary);
            response.setProjectOverview(projectOverview);
            response.setFrontendArchitecture(frontendArchitecture);
            response.setBackendArchitecture(backendArchitecture);
            response.setDatabaseArchitecture(databaseArchitecture);
            response.setApiArchitecture(apiArchitecture);
            response.setServiceCommunication(serviceCommunication);
            response.setModuleDependencies(moduleDependencies);
            response.setDeploymentArchitecture(deploymentArchitecture);
            response.setScalabilityRecommendations(scalabilityRecommendations);
            response.setPerformanceRecommendations(performanceRecommendations);
            response.setCachingStrategy(cachingStrategy);
            response.setLoadBalancingStrategy(loadBalancingStrategy);
            response.setSecurityArchitecture(securityArchitecture);
            response.setAuthenticationFlow(authenticationFlow);
            response.setAuthorizationFlow(authorizationFlow);
            response.setMonitoringRecommendations(monitoringRecommendations);
            response.setLoggingRecommendations(loggingRecommendations);
            response.setDisasterRecoveryRecommendations(disasterRecoveryRecommendations);
            response.setTechnologyRecommendations(technologyRecommendations);
            response.setFutureImprovements(futureImprovements);
            response.setGeneratedSystemDesignMarkdown(generatedSystemDesignMarkdown);
            response.setGeneratedMermaidFlowDiagram(generatedMermaidFlowDiagram);
            response.setDeploymentSummary(deploymentSummary);
            response.setDockerRecommendations(dockerRecommendations);
            response.setDockerfile(dockerfile);
            response.setDockerCompose(dockerCompose);
            response.setKubernetesManifest(kubernetesManifest);
            response.setGithubActionsWorkflow(githubActionsWorkflow);
            response.setEnvironmentVariables(environmentVariables);
            response.setSecretsRequired(secretsRequired);
            response.setDeploymentSteps(deploymentSteps);
            response.setCiCdFlow(ciCdFlow);
            response.setMonitoringRecommendations(deploymentMonitoringRecommendations.isEmpty() ? monitoringRecommendations : deploymentMonitoringRecommendations);
            response.setLoggingRecommendations(deploymentLoggingRecommendations.isEmpty() ? loggingRecommendations : deploymentLoggingRecommendations);
            response.setScalingRecommendations(deploymentScalingRecommendations);
            response.setBackupStrategy(backupStrategy);
            response.setRollbackStrategy(rollbackStrategy);
            response.setCloudRecommendations(cloudRecommendations);
            response.setEstimatedDeploymentCost(estimatedDeploymentCost);
            response.setGeneratedMarkdown(generatedMarkdown);
            return response;
        } catch (Exception ex) {
            return new AiReviewResponse(0, "Review parsing failed: " + ex.getMessage(), reviewType, new ArrayList<>());
        }
    }

    private List<String> parseStringArray(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node == null || node.isNull()) {
            return values;
        }
        if (node.isArray()) {
            for (JsonNode entry : node) {
                if (entry.isTextual()) {
                    addParsedValue(values, entry.asText());
                } else if (entry.isValueNode()) {
                    addParsedValue(values, entry.toString());
                }
            }
        } else if (node.isTextual()) {
            addParsedValue(values, node.asText());
        } else if (node.isValueNode()) {
            addParsedValue(values, node.toString());
        }
        return values;
    }

    private void addParsedValue(List<String> values, String rawValue) {
        if (rawValue == null) {
            return;
        }
        String trimmed = rawValue.trim();
        if (trimmed.isBlank()) {
            return;
        }
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json|markdown|md|yaml|txt|text)?\\s*", "").replaceFirst("\\s*```$", "");
        }
        String[] lines = trimmed.split("\\R");
        for (String line : lines) {
            String candidate = line.trim();
            if (candidate.isBlank() || candidate.startsWith("```")) {
                continue;
            }
            if (candidate.startsWith("- ") || candidate.startsWith("* ") || candidate.startsWith("• ")) {
                candidate = candidate.replaceFirst("^[\\-*•]\\s*", "");
            }
            if (!candidate.isBlank()) {
                values.add(candidate);
            }
        }
    }

    private String normalizeJsonPayload(String rawPayload) {
        if (rawPayload == null || rawPayload.isBlank()) {
            return rawPayload;
        }
        String normalized = rawPayload.trim();
        if (normalized.startsWith("```") && normalized.endsWith("```")) {
            normalized = normalized.replaceFirst("^```(?:json|JSON|markdown|md|yaml|txt|text)?\\s*", "").replaceFirst("\\s*```$", "");
        }
        int objectStart = normalized.indexOf('{');
        int objectEnd = normalized.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            normalized = normalized.substring(objectStart, objectEnd + 1);
        }
        normalized = normalized.replaceAll("\\r\\n", "\\n");
        normalized = normalized.replaceAll("(?<!\\\\)\\n", "\\n");
        return normalized;
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
