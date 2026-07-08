package com.yoonus.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.dto.WorkspaceAnalysisRequest;
import com.yoonus.backend.dto.WorkspaceAnalysisResponse;
import com.yoonus.backend.dto.WorkspaceContextRequest;
import com.yoonus.backend.dto.WorkspaceContextResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.ProjectVersionService;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiGenerationHistoryRepository generationHistoryRepository;

    @Mock
    private AiCodeGenerator aiCodeGenerator;

    @Mock
    private ProjectVersionService projectVersionService;

    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiService = new AiServiceImpl(projectRepository, userRepository, generationHistoryRepository, aiCodeGenerator, projectVersionService, new ObjectMapper());
    }

    @Test
    void generateCode_shouldCreateProjectAndReturnResponse() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        AiGenerationRequest request = new AiGenerationRequest();
        request.setTitle("AI App");
        request.setDescription("Landing page");
        request.setPrompt("Create a landing page");
        request.setFramework("React");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.generateCode("Create a landing page", "React")).thenReturn("export default function App() {};");
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(generationHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AiGenerationResponse response = aiService.generateCode("owner@example.com", request);

        assertNotNull(response);
        assertEquals("AI App", response.getTitle());
        assertEquals(ProjectStatus.COMPLETED, response.getStatus());
        assertEquals("export default function App() {};", response.getGeneratedCode());
        verify(projectRepository).save(any(Project.class));
        verify(generationHistoryRepository).save(any());
    }

    @Test
    void generateCode_shouldReturnFailedStatusWhenGeneratorThrows() {
        User owner = new User();
        owner.setId(2L);
        owner.setEmail("owner2@example.com");

        AiGenerationRequest request = new AiGenerationRequest();
        request.setTitle("Fallback App");
        request.setDescription("Landing page");
        request.setPrompt("Create a landing page");
        request.setFramework("React");

        when(userRepository.findByEmail("owner2@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.generateCode("Create a landing page", "React"))
                .thenThrow(new IllegalStateException("API unavailable"));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(generationHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AiGenerationResponse response = aiService.generateCode("owner2@example.com", request);

        assertNotNull(response);
        assertEquals("Fallback App", response.getTitle());
        assertEquals(ProjectStatus.FAILED, response.getStatus());
        assertEquals("AI generation failed: API unavailable", response.getMessage());
        verify(projectRepository).save(any(Project.class));
        verify(generationHistoryRepository).save(any());
    }

    @Test
    void reviewCode_shouldReturnStructuredReviewResponse() {
        User owner = new User();
        owner.setId(3L);
        owner.setEmail("reviewer@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("bug-fix");
        AiReviewRequest.ReviewFileInput file = new AiReviewRequest.ReviewFileInput();
        file.setPath("src/App.tsx");
        file.setCode("export default function App() { return <div>Hi</div>; }");
        file.setLanguage("tsx");
        request.setFiles(java.util.List.of(file));

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":82,\"summary\":\"Looks solid\",\"reviewType\":\"bug-fix\",\"suggestions\":[{\"id\":\"1\",\"severity\":\"medium\",\"title\":\"Add null guard\",\"file\":\"src/App.tsx\",\"description\":\"Guard against null\",\"suggestedFix\":\"return null;\"}]}" );

        AiReviewResponse response = aiService.reviewCode("reviewer@example.com", request);

        assertNotNull(response);
        assertEquals(82, response.getOverallScore());
        assertEquals("bug-fix", response.getReviewType());
        assertEquals(1, response.getSuggestions().size());
        assertEquals("src/App.tsx", response.getSuggestions().get(0).getFile());
    }

    @Test
    void reviewCode_shouldSupportSingleFileReviewPayload() {
        User owner = new User();
        owner.setId(4L);
        owner.setEmail("reviewer2@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("security");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>{userInput}</div>; }");

        when(userRepository.findByEmail("reviewer2@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":88,\"summary\":\"Single file review\",\"reviewType\":\"security\",\"severity\":\"high\",\"issues\":[\"Potential XSS\"],\"suggestedFixes\":[\"Escape user content\"],\"optimizedCode\":\"export default function App() { return <div>{safeValue}</div>; }\"}");

        AiReviewResponse response = aiService.reviewCode("reviewer2@example.com", request);

        assertNotNull(response);
        assertEquals("Single file review", response.getSummary());
        assertEquals("high", response.getSeverity());
        assertEquals(1, response.getIssues().size());
        assertEquals(1, response.getSuggestedFixes().size());
        assertEquals("export default function App() { return <div>{safeValue}</div>; }", response.getOptimizedCode());
    }

    @Test
    void reviewCode_shouldParseRefactoringPayloadWithEstimatedImprovement() {
        User owner = new User();
        owner.setId(5L);
        owner.setEmail("refactorer@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("refactor");
        request.setRefactoringType("Extract Method");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App(){ return <div>{value}</div>; }");

        when(userRepository.findByEmail("refactorer@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":91,\"summary\":\"Extracted helper\",\"reviewType\":\"refactor\",\"severity\":\"low\",\"estimatedImprovement\":24,\"suggestions\":[{\"id\":\"1\",\"severity\":\"low\",\"title\":\"Extract helper\",\"file\":\"src/App.tsx\",\"description\":\"Move rendering into a helper\",\"suggestedFix\":\"function renderContent(){ return <div>{value}</div>; }\",\"lineNumber\":\"3\",\"confidenceScore\":0.92}],\"optimizedCode\":\"export default function App(){ return renderContent(); }\"}");

        AiReviewResponse response = aiService.reviewCode("refactorer@example.com", request);

        assertNotNull(response);
        assertEquals(91, response.getOverallScore());
        assertEquals("refactor", response.getReviewType());
        assertEquals(24, response.getEstimatedImprovement());
        assertEquals(1, response.getSuggestions().size());
        assertEquals("Extract helper", response.getSuggestions().get(0).getTitle());
    }

    @Test
    void reviewCode_shouldParseDocumentationPayload() {
        User owner = new User();
        owner.setId(6L);
        owner.setEmail("docwriter@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("documentation-generator");
        request.setDocumentationType("Function documentation");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("docwriter@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":95,\"summary\":\"Added docs\",\"reviewType\":\"documentation-generator\",\"documentationType\":\"Function documentation\",\"generatedDocumentation\":\"/** Renders the app shell. */\",\"suggestedInsertionPoints\":[\"Above the App component\"]}");

        AiReviewResponse response = aiService.reviewCode("docwriter@example.com", request);

        assertNotNull(response);
        assertEquals("Added docs", response.getSummary());
        assertEquals("Function documentation", response.getDocumentationType());
        assertEquals("/** Renders the app shell. */", response.getGeneratedDocumentation());
        assertEquals(1, response.getSuggestedInsertionPoints().size());
    }

    @Test
    void reviewCode_shouldParseExplanationPayload() {
        User owner = new User();
        owner.setId(7L);
        owner.setEmail("explainer@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("explain");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("explainer@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":97,\"summary\":\"Explained the component\",\"reviewType\":\"explain\",\"purpose\":\"Renders the app shell\",\"algorithm\":\"Return JSX markup\",\"complexity\":\"O(1)\",\"importantFunctions\":[\"App\"],\"dependencies\":[\"React\"],\"potentialImprovements\":[\"Add props\"],\"beginnerExplanation\":\"This component outputs a simple greeting\",\"advancedExplanation\":\"It is a stateless functional component that returns markup\"}");

        AiReviewResponse response = aiService.reviewCode("explainer@example.com", request);

        assertNotNull(response);
        assertEquals("Explained the component", response.getSummary());
        assertEquals("Renders the app shell", response.getPurpose());
        assertEquals("Return JSX markup", response.getAlgorithm());
        assertEquals("O(1)", response.getComplexity());
        assertEquals(1, response.getImportantFunctions().size());
        assertEquals("React", response.getDependencies().get(0));
        assertEquals("Add props", response.getPotentialImprovements().get(0));
    }

    @Test
    void reviewCode_shouldParseArchitecturePayload() {
        User owner = new User();
        owner.setId(8L);
        owner.setEmail("architect@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("architecture");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("architect@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":96,\"summary\":\"Architecture overview generated\",\"reviewType\":\"architecture\",\"architectureSummary\":\"Single-page React application\",\"applicationLayers\":[\"Presentation\"],\"frontendStructure\":[\"React components\"],\"backendStructure\":[\"Spring Boot services\"],\"databaseEntities\":[\"User\"],\"serviceDependencies\":[\"Auth service\"],\"apiFlow\":[\"Client to API\"],\"componentRelationships\":[\"UI to service\"],\"suggestedImprovements\":[\"Add modular layout\"],\"scalabilityRecommendations\":[\"Introduce lazy loading\"],\"securityRecommendations\":[\"Add auth guards\"]}");

        AiReviewResponse response = aiService.reviewCode("architect@example.com", request);

        assertNotNull(response);
        assertEquals("Architecture overview generated", response.getSummary());
        assertEquals("Single-page React application", response.getArchitectureSummary());
        assertEquals(1, response.getApplicationLayers().size());
        assertEquals("React components", response.getFrontendStructure().get(0));
        assertEquals("Spring Boot services", response.getBackendStructure().get(0));
        assertEquals("User", response.getDatabaseEntities().get(0));
        assertEquals("Auth service", response.getServiceDependencies().get(0));
        assertEquals("Client to API", response.getApiFlow().get(0));
        assertEquals("UI to service", response.getComponentRelationships().get(0));
        assertEquals("Add modular layout", response.getSuggestedImprovements().get(0));
        assertEquals("Introduce lazy loading", response.getScalabilityRecommendations().get(0));
        assertEquals("Add auth guards", response.getSecurityRecommendations().get(0));
    }

    @Test
    void reviewCode_shouldParseDatabasePayload() {
        User owner = new User();
        owner.setId(9L);
        owner.setEmail("dbdesigner@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("database");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("dbdesigner@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":95,\"summary\":\"Database design generated\",\"reviewType\":\"database\",\"databaseSummary\":\"PostgreSQL-backed application\",\"entities\":[\"User\"],\"relationships\":[\"User has many Projects\"],\"primaryKeys\":[\"id\"],\"foreignKeys\":[\"user_id references users.id\"],\"indexes\":[\"idx_users_email\"],\"constraints\":[\"NOT NULL\"],\"normalizationSuggestions\":[\"Split profile data\"],\"missingTables\":[\"project_members\"],\"migrationSuggestions\":[\"Add new table\"],\"postgresRecommendations\":[\"Use UUID primary keys\"],\"generatedSqlSchema\":\"CREATE TABLE users (id BIGSERIAL PRIMARY KEY);\",\"generatedMermaidERDiagram\":\"erDiagram\n  USERS {\n    bigint id\n  }\"}");

        AiReviewResponse response = aiService.reviewCode("dbdesigner@example.com", request);

        assertNotNull(response);
        assertEquals("Database design generated", response.getSummary());
        assertEquals("PostgreSQL-backed application", response.getDatabaseSummary());
        assertEquals(1, response.getEntities().size());
        assertEquals("User", response.getEntities().get(0));
        assertEquals("User has many Projects", response.getRelationships().get(0));
        assertEquals("id", response.getPrimaryKeys().get(0));
        assertEquals("user_id references users.id", response.getForeignKeys().get(0));
        assertEquals("idx_users_email", response.getIndexes().get(0));
        assertEquals("NOT NULL", response.getConstraints().get(0));
        assertEquals("Split profile data", response.getNormalizationSuggestions().get(0));
        assertEquals("project_members", response.getMissingTables().get(0));
        assertEquals("Add new table", response.getMigrationSuggestions().get(0));
        assertEquals("Use UUID primary keys", response.getPostgresRecommendations().get(0));
        assertEquals("CREATE TABLE users (id BIGSERIAL PRIMARY KEY);", response.getGeneratedSqlSchema());
        assertTrue(response.getGeneratedMermaidERDiagram().contains("erDiagram"));
    }

    @Test
    void reviewCode_shouldParseApiPayload() {
        User owner = new User();
        owner.setId(10L);
        owner.setEmail("apidesigner@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("api");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("apidesigner@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":94,\"summary\":\"API design generated\",\"reviewType\":\"api\",\"apiSummary\":\"REST endpoints for project resources\",\"resources\":[\"Project\"],\"endpoints\":[\"GET /projects\"],\"requestDtos\":[\"CreateProjectRequest\"],\"responseDtos\":[\"ProjectResponse\"],\"validationRules\":[\"Name is required\"],\"authenticationRequirements\":[\"JWT bearer token\"],\"authorizationRequirements\":[\"User must own the project\"],\"httpStatusCodes\":[\"200 OK\"],\"errorResponses\":[\"401 Unauthorized\"],\"openApiSpec\":\"openapi: 3.0.0\",\"curlExamples\":[\"curl -X GET /projects\"],\"postmanExamples\":[\"GET /projects\"],\"improvementSuggestions\":[\"Add pagination\"]}");

        AiReviewResponse response = aiService.reviewCode("apidesigner@example.com", request);

        assertNotNull(response);
        assertEquals("API design generated", response.getSummary());
        assertEquals("REST endpoints for project resources", response.getApiSummary());
        assertEquals(1, response.getResources().size());
        assertEquals("Project", response.getResources().get(0));
        assertEquals("GET /projects", response.getEndpoints().get(0));
        assertEquals("CreateProjectRequest", response.getRequestDtos().get(0));
        assertEquals("ProjectResponse", response.getResponseDtos().get(0));
        assertEquals("curl -X GET /projects", response.getCurlExamples().get(0));
        assertEquals("GET /projects", response.getPostmanExamples().get(0));
        assertEquals("openapi: 3.0.0", response.getOpenApiSpec());
        assertEquals("Add pagination", response.getImprovementSuggestions().get(0));
    }

    @Test
    void reviewCode_shouldParseDevopsPayloadWithMultilineYaml() {
        User owner = new User();
        owner.setId(11L);
        owner.setEmail("devops@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("devops");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("devops@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":97,\"summary\":\"Deployment plan generated\",\"reviewType\":\"devops\",\"deploymentSummary\":\"Containerize and deploy the app\",\"dockerRecommendations\":[\"Use a slim node image\"],\"dockerfile\":\"FROM node:20\\nWORKDIR /app\\nCOPY package.json ./\\nRUN npm install\",\"dockerCompose\":\"services:\\n  app:\\n    build: .\",\"kubernetesManifest\":\"apiVersion: apps/v1\\nkind: Deployment\",\"githubActionsWorkflow\":\"name: build\\non: push\",\"environmentVariables\":[\"PORT=3000\"],\"secretsRequired\":[\"API_TOKEN\"],\"deploymentSteps\":[\"Build image\"],\"ciCdFlow\":[\"Run tests\"],\"monitoringRecommendations\":[\"Enable Prometheus\"],\"loggingRecommendations\":[\"Use structured logs\"],\"scalingRecommendations\":[\"Set CPU target\"],\"backupStrategy\":\"Daily backups\",\"rollbackStrategy\":\"Revert deployment\",\"securityRecommendations\":[\"Use non-root user\"],\"cloudRecommendations\":[\"Deploy to Azure Container Apps\"],\"estimatedDeploymentCost\":\"$120/month\",\"generatedMarkdown\":\"# Deployment Plan\\n\\n## Summary\\nContainerize and deploy the app\"}");

        AiReviewResponse response = aiService.reviewCode("devops@example.com", request);

        assertNotNull(response);
        assertEquals("devops", response.getReviewType());
        assertEquals("Containerize and deploy the app", response.getDeploymentSummary());
        assertEquals("FROM node:20\nWORKDIR /app\nCOPY package.json ./\nRUN npm install", response.getDockerfile());
        assertEquals("services:\n  app:\n    build: .", response.getDockerCompose());
        assertEquals("apiVersion: apps/v1\nkind: Deployment", response.getKubernetesManifest());
        assertEquals("name: build\non: push", response.getGithubActionsWorkflow());
        assertEquals(1, response.getDockerRecommendations().size());
        assertEquals("Use a slim node image", response.getDockerRecommendations().get(0));
        assertEquals("$120/month", response.getEstimatedDeploymentCost());
        assertTrue(response.getGeneratedMarkdown().contains("# Deployment Plan"));
    }

    @Test
    void reviewCode_shouldParseSecurityPayloadWithMarkdownAndThreatModel() {
        User owner = new User();
        owner.setId(12L);
        owner.setEmail("securityreviewer@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("security");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>{userInput}</div>; }");

        when(userRepository.findByEmail("securityreviewer@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":99,\"summary\":\"Security review generated\",\"reviewType\":\"security\",\"securitySummary\":\"The app needs stronger input validation\",\"vulnerabilities\":[\"Stored XSS\"],\"owaspTop10\":[\"A03:2021 Injection\"],\"authenticationIssues\":[\"No MFA\"],\"authorizationIssues\":[\"Missing role checks\"],\"jwtRecommendations\":[\"Rotate signing keys\"],\"csrfRecommendations\":[\"Add CSRF token validation\"],\"xssRecommendations\":[\"Escape user input\"],\"sqlInjectionRisks\":[\"String concatenation in queries\"],\"secretsFound\":[\"API token in source\"],\"dependencyRisks\":[\"Deprecated package\"],\"insecureConfigurations\":[\"CORS allows all origins\"],\"encryptionRecommendations\":[\"Enable TLS 1.3\"],\"inputValidationRecommendations\":[\"Validate every request body\"],\"securityHeaders\":[\"Content-Security-Policy\"],\"rateLimitingRecommendations\":[\"Throttle login attempts\"],\"loggingAuditRecommendations\":[\"Emit audit trail\"],\"complianceRecommendations\":[\"Align with SOC 2\"],\"generatedSecurityChecklist\":\"- [x] Review auth\n- [ ] Review secrets\",\"generatedThreatModel\":\"## Threat Model\\n\\n- Spoofing\\n- Tampering\",\"generatedSecurityMarkdown\":\"# Security Review\\n\\n## Summary\\nThe app needs stronger input validation\"}");

        AiReviewResponse response = aiService.reviewCode("securityreviewer@example.com", request);

        assertNotNull(response);
        assertEquals("Security review generated", response.getSummary());
        assertEquals("The app needs stronger input validation", response.getSecuritySummary());
        assertEquals("Stored XSS", response.getVulnerabilities().get(0));
        assertEquals("A03:2021 Injection", response.getOwaspTop10().get(0));
        assertTrue(response.getGeneratedSecurityChecklist().contains("Review auth"));
        assertTrue(response.getGeneratedThreatModel().contains("Threat Model"));
        assertTrue(response.getGeneratedSecurityMarkdown().contains("# Security Review"));
    }

    @Test
    void reviewCode_shouldParseSystemPayload() {
        User owner = new User();
        owner.setId(12L);
        owner.setEmail("systemdesigner@example.com");

        AiReviewRequest request = new AiReviewRequest();
        request.setReviewType("system");
        request.setFileName("src/App.tsx");
        request.setFileContent("export default function App() { return <div>Hello</div>; }");

        when(userRepository.findByEmail("systemdesigner@example.com")).thenReturn(Optional.of(owner));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"overallScore\":98,\"summary\":\"System design generated\",\"reviewType\":\"system\",\"systemSummary\":\"A modular SaaS system\",\"projectOverview\":\"Multi-tenant product\",\"frontendArchitecture\":[\"React SPA\"],\"backendArchitecture\":[\"Spring Boot services\"],\"databaseArchitecture\":[\"PostgreSQL\"],\"apiArchitecture\":[\"REST API\"],\"serviceCommunication\":[\"Async messaging\"],\"moduleDependencies\":[\"Auth module\"],\"deploymentArchitecture\":[\"Containerized deployment\"],\"scalabilityRecommendations\":[\"Horizontal scaling\"],\"performanceRecommendations\":[\"Cache frequently used queries\"],\"cachingStrategy\":\"Redis\",\"loadBalancingStrategy\":\"Round robin\",\"securityArchitecture\":[\"Zero trust\"],\"authenticationFlow\":[\"OIDC login\"],\"authorizationFlow\":[\"RBAC\"],\"monitoringRecommendations\":[\"Prometheus\"],\"loggingRecommendations\":[\"Structured logs\"],\"disasterRecoveryRecommendations\":[\"Daily backups\"],\"technologyRecommendations\":[\"Kubernetes\"],\"futureImprovements\":[\"Event-driven architecture\"],\"generatedSystemDesignMarkdown\":\"# System Design\",\"generatedMermaidFlowDiagram\":\"flowchart TD\"}");

        AiReviewResponse response = aiService.reviewCode("systemdesigner@example.com", request);

        assertNotNull(response);
        assertEquals("System design generated", response.getSummary());
        assertEquals("A modular SaaS system", response.getSystemSummary());
        assertEquals("Multi-tenant product", response.getProjectOverview());
        assertEquals("React SPA", response.getFrontendArchitecture().get(0));
        assertEquals("Redis", response.getCachingStrategy());
        assertEquals("Round robin", response.getLoadBalancingStrategy());
        assertEquals("# System Design", response.getGeneratedSystemDesignMarkdown());
        assertEquals("flowchart TD", response.getGeneratedMermaidFlowDiagram());
    }

    @Test
    void buildWorkspaceContext_shouldSummarizeProjectAndFiles() {
        User owner = new User();
        owner.setId(13L);
        owner.setEmail("workspace@example.com");

        Project project = new Project();
        project.setId(77L);
        project.setTitle("Analytics Dashboard");
        project.setDescription("A dashboard app");
        project.setPrompt("Build an analytics dashboard");
        project.setFramework("React");

        WorkspaceContextRequest request = new WorkspaceContextRequest();
        request.setProjectId(77L);
        request.setPrompt("Build an analytics dashboard");
        request.setAnalysisMode("architecture");
        request.setSelectedPaths(List.of("src/App.tsx", "src/main.tsx"));
        request.setFiles(List.of(
                new WorkspaceContextRequest.WorkspaceFileInput("src/App.tsx", "export default function App() { return <div />; }", "tsx"),
                new WorkspaceContextRequest.WorkspaceFileInput("src/main.tsx", "import ReactDOM from 'react-dom/client';", "tsx")
        ));

        when(userRepository.findByEmail("workspace@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findById(77L)).thenReturn(Optional.of(project));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"projectSummary\":\"Analytics Dashboard\",\"keyFiles\":[\"src/App.tsx\", \"src/main.tsx\"],\"riskFlags\":[\"Missing tests\"]}");

        WorkspaceContextResponse response = aiService.buildWorkspaceContext("workspace@example.com", request);

        assertNotNull(response);
        assertEquals(77L, response.getProjectId());
        assertTrue(response.getProjectSummary().contains("Analytics Dashboard"));
        assertEquals(2, response.getKeyFiles().size());
        assertFalse(response.getRiskFlags().isEmpty());
    }

    @Test
    void analyzeWorkspace_shouldReturnCrossFileFindings() {
        User owner = new User();
        owner.setId(14L);
        owner.setEmail("agent@example.com");

        Project project = new Project();
        project.setId(88L);
        project.setTitle("Agent Workspace");
        project.setDescription("A workspace app");
        project.setPrompt("Create an agent workspace");
        project.setFramework("React");

        WorkspaceAnalysisRequest request = new WorkspaceAnalysisRequest();
        request.setProjectId(88L);
        request.setPrompt("Review the project as a whole");
        request.setAnalysisMode("architecture");
        request.setSelectedPaths(List.of("src/App.tsx", "src/api.ts"));
        request.setFiles(List.of(
                new WorkspaceAnalysisRequest.WorkspaceFileInput("src/App.tsx", "console.log('debug');\nexport default function App() { return <div />; }", "tsx"),
                new WorkspaceAnalysisRequest.WorkspaceFileInput("src/api.ts", "export const apiBase = '/api';", "ts")
        ));

        when(userRepository.findByEmail("agent@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findById(88L)).thenReturn(Optional.of(project));
        when(aiCodeGenerator.reviewCode(any(), any())).thenReturn("{\"summary\":\"Project-wide analysis completed.\",\"findings\":[\"A finding\"],\"crossFileIssues\":[\"console.log('debug') left in App.tsx\"],\"suggestedNextActions\":[\"Review the code\"]}");

        WorkspaceAnalysisResponse response = aiService.analyzeWorkspace("agent@example.com", request);

        assertNotNull(response);
        assertFalse(response.getFindings().isEmpty());
        assertTrue(response.getCrossFileIssues().stream().anyMatch(issue -> issue.toLowerCase().contains("debug")) || response.getSuggestedNextActions().stream().anyMatch(action -> action.toLowerCase().contains("review")));
    }
}
