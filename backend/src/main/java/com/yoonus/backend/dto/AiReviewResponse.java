package com.yoonus.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AiReviewResponse {

    private int overallScore;
    private String summary;
    private String reviewType;
    private String severity;
    private Double confidenceScore;
    private Integer estimatedImprovement;
    private List<String> issues = new ArrayList<>();
    private List<String> suggestedFixes = new ArrayList<>();
    private String optimizedCode;
    private String generatedTestCode;
    private String testSummary;
    private String frameworkUsed;
    private Integer estimatedCoverage;
    private List<String> edgeCasesCovered = new ArrayList<>();
    private String generatedDocumentation;
    private String documentationType;
    private List<String> suggestedInsertionPoints = new ArrayList<>();
    private String purpose;
    private String algorithm;
    private String complexity;
    private List<String> importantFunctions = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();
    private List<String> potentialImprovements = new ArrayList<>();
    private String beginnerExplanation;
    private String advancedExplanation;
    private String architectureSummary;
    private List<String> applicationLayers = new ArrayList<>();
    private List<String> frontendStructure = new ArrayList<>();
    private List<String> backendStructure = new ArrayList<>();
    private List<String> databaseEntities = new ArrayList<>();
    private List<String> serviceDependencies = new ArrayList<>();
    private List<String> apiFlow = new ArrayList<>();
    private List<String> componentRelationships = new ArrayList<>();
    private List<String> suggestedImprovements = new ArrayList<>();
    private List<String> securityRecommendations = new ArrayList<>();
    private String securitySummary;
    private List<String> vulnerabilities = new ArrayList<>();
    private List<String> owaspTop10 = new ArrayList<>();
    private List<String> authenticationIssues = new ArrayList<>();
    private List<String> authorizationIssues = new ArrayList<>();
    private List<String> jwtRecommendations = new ArrayList<>();
    private List<String> csrfRecommendations = new ArrayList<>();
    private List<String> xssRecommendations = new ArrayList<>();
    private List<String> sqlInjectionRisks = new ArrayList<>();
    private List<String> secretsFound = new ArrayList<>();
    private List<String> dependencyRisks = new ArrayList<>();
    private List<String> insecureConfigurations = new ArrayList<>();
    private List<String> encryptionRecommendations = new ArrayList<>();
    private List<String> inputValidationRecommendations = new ArrayList<>();
    private List<String> securityHeaders = new ArrayList<>();
    private List<String> rateLimitingRecommendations = new ArrayList<>();
    private List<String> loggingAuditRecommendations = new ArrayList<>();
    private List<String> complianceRecommendations = new ArrayList<>();
    private String generatedSecurityChecklist;
    private String generatedThreatModel;
    private String generatedSecurityMarkdown;
    private String databaseSummary;
    private List<String> entities = new ArrayList<>();
    private List<String> relationships = new ArrayList<>();
    private List<String> primaryKeys = new ArrayList<>();
    private List<String> foreignKeys = new ArrayList<>();
    private List<String> indexes = new ArrayList<>();
    private List<String> constraints = new ArrayList<>();
    private List<String> normalizationSuggestions = new ArrayList<>();
    private List<String> missingTables = new ArrayList<>();
    private List<String> migrationSuggestions = new ArrayList<>();
    private List<String> postgresRecommendations = new ArrayList<>();
    private String generatedSqlSchema;
    private String generatedMermaidERDiagram;
    private String apiSummary;
    private List<String> resources = new ArrayList<>();
    private List<String> endpoints = new ArrayList<>();
    private List<String> requestDtos = new ArrayList<>();
    private List<String> responseDtos = new ArrayList<>();
    private List<String> validationRules = new ArrayList<>();
    private List<String> authenticationRequirements = new ArrayList<>();
    private List<String> authorizationRequirements = new ArrayList<>();
    private List<String> httpStatusCodes = new ArrayList<>();
    private List<String> errorResponses = new ArrayList<>();
    private String openApiSpec;
    private List<String> curlExamples = new ArrayList<>();
    private List<String> postmanExamples = new ArrayList<>();
    private List<String> improvementSuggestions = new ArrayList<>();
    private String systemSummary;
    private String projectOverview;
    private List<String> frontendArchitecture = new ArrayList<>();
    private List<String> backendArchitecture = new ArrayList<>();
    private List<String> databaseArchitecture = new ArrayList<>();
    private List<String> apiArchitecture = new ArrayList<>();
    private List<String> serviceCommunication = new ArrayList<>();
    private List<String> moduleDependencies = new ArrayList<>();
    private List<String> deploymentArchitecture = new ArrayList<>();
    private List<String> scalabilityRecommendations = new ArrayList<>();
    private List<String> performanceRecommendations = new ArrayList<>();
    private String cachingStrategy;
    private String loadBalancingStrategy;
    private List<String> securityArchitecture = new ArrayList<>();
    private List<String> authenticationFlow = new ArrayList<>();
    private List<String> authorizationFlow = new ArrayList<>();
    private List<String> monitoringRecommendations = new ArrayList<>();
    private List<String> loggingRecommendations = new ArrayList<>();
    private List<String> disasterRecoveryRecommendations = new ArrayList<>();
    private List<String> technologyRecommendations = new ArrayList<>();
    private List<String> futureImprovements = new ArrayList<>();
    private String generatedSystemDesignMarkdown;
    private String generatedMermaidFlowDiagram;
    private String deploymentSummary;
    private List<String> dockerRecommendations = new ArrayList<>();
    private String dockerfile;
    private String dockerCompose;
    private String kubernetesManifest;
    private String githubActionsWorkflow;
    private List<String> environmentVariables = new ArrayList<>();
    private List<String> secretsRequired = new ArrayList<>();
    private List<String> deploymentSteps = new ArrayList<>();
    private List<String> ciCdFlow = new ArrayList<>();
    private List<String> scalingRecommendations = new ArrayList<>();
    private String backupStrategy;
    private String rollbackStrategy;
    private List<String> cloudRecommendations = new ArrayList<>();
    private String estimatedDeploymentCost;
    private String generatedMarkdown;
    private List<ReviewSuggestion> suggestions = new ArrayList<>();

    public AiReviewResponse() {
    }

    public AiReviewResponse(int overallScore, String summary, String reviewType, List<ReviewSuggestion> suggestions) {
        this.overallScore = overallScore;
        this.summary = summary;
        this.reviewType = reviewType;
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getEstimatedImprovement() {
        return estimatedImprovement;
    }

    public void setEstimatedImprovement(Integer estimatedImprovement) {
        this.estimatedImprovement = estimatedImprovement;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues == null ? new ArrayList<>() : issues;
    }

    public List<String> getSuggestedFixes() {
        return suggestedFixes;
    }

    public void setSuggestedFixes(List<String> suggestedFixes) {
        this.suggestedFixes = suggestedFixes == null ? new ArrayList<>() : suggestedFixes;
    }

    public String getOptimizedCode() {
        return optimizedCode;
    }

    public void setOptimizedCode(String optimizedCode) {
        this.optimizedCode = optimizedCode;
    }

    public String getGeneratedTestCode() {
        return generatedTestCode;
    }

    public void setGeneratedTestCode(String generatedTestCode) {
        this.generatedTestCode = generatedTestCode;
    }

    public String getTestSummary() {
        return testSummary;
    }

    public void setTestSummary(String testSummary) {
        this.testSummary = testSummary;
    }

    public String getFrameworkUsed() {
        return frameworkUsed;
    }

    public void setFrameworkUsed(String frameworkUsed) {
        this.frameworkUsed = frameworkUsed;
    }

    public Integer getEstimatedCoverage() {
        return estimatedCoverage;
    }

    public void setEstimatedCoverage(Integer estimatedCoverage) {
        this.estimatedCoverage = estimatedCoverage;
    }

    public List<String> getEdgeCasesCovered() {
        return edgeCasesCovered;
    }

    public void setEdgeCasesCovered(List<String> edgeCasesCovered) {
        this.edgeCasesCovered = edgeCasesCovered == null ? new ArrayList<>() : edgeCasesCovered;
    }

    public String getGeneratedDocumentation() {
        return generatedDocumentation;
    }

    public void setGeneratedDocumentation(String generatedDocumentation) {
        this.generatedDocumentation = generatedDocumentation;
    }

    public String getDocumentationType() {
        return documentationType;
    }

    public void setDocumentationType(String documentationType) {
        this.documentationType = documentationType;
    }

    public List<String> getSuggestedInsertionPoints() {
        return suggestedInsertionPoints;
    }

    public void setSuggestedInsertionPoints(List<String> suggestedInsertionPoints) {
        this.suggestedInsertionPoints = suggestedInsertionPoints == null ? new ArrayList<>() : suggestedInsertionPoints;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public List<String> getImportantFunctions() {
        return importantFunctions;
    }

    public void setImportantFunctions(List<String> importantFunctions) {
        this.importantFunctions = importantFunctions == null ? new ArrayList<>() : importantFunctions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies == null ? new ArrayList<>() : dependencies;
    }

    public List<String> getPotentialImprovements() {
        return potentialImprovements;
    }

    public void setPotentialImprovements(List<String> potentialImprovements) {
        this.potentialImprovements = potentialImprovements == null ? new ArrayList<>() : potentialImprovements;
    }

    public String getBeginnerExplanation() {
        return beginnerExplanation;
    }

    public void setBeginnerExplanation(String beginnerExplanation) {
        this.beginnerExplanation = beginnerExplanation;
    }

    public String getAdvancedExplanation() {
        return advancedExplanation;
    }

    public void setAdvancedExplanation(String advancedExplanation) {
        this.advancedExplanation = advancedExplanation;
    }

    public String getArchitectureSummary() {
        return architectureSummary;
    }

    public void setArchitectureSummary(String architectureSummary) {
        this.architectureSummary = architectureSummary;
    }

    public List<String> getApplicationLayers() {
        return applicationLayers;
    }

    public void setApplicationLayers(List<String> applicationLayers) {
        this.applicationLayers = applicationLayers == null ? new ArrayList<>() : applicationLayers;
    }

    public List<String> getFrontendStructure() {
        return frontendStructure;
    }

    public void setFrontendStructure(List<String> frontendStructure) {
        this.frontendStructure = frontendStructure == null ? new ArrayList<>() : frontendStructure;
    }

    public List<String> getBackendStructure() {
        return backendStructure;
    }

    public void setBackendStructure(List<String> backendStructure) {
        this.backendStructure = backendStructure == null ? new ArrayList<>() : backendStructure;
    }

    public List<String> getDatabaseEntities() {
        return databaseEntities;
    }

    public void setDatabaseEntities(List<String> databaseEntities) {
        this.databaseEntities = databaseEntities == null ? new ArrayList<>() : databaseEntities;
    }

    public List<String> getServiceDependencies() {
        return serviceDependencies;
    }

    public void setServiceDependencies(List<String> serviceDependencies) {
        this.serviceDependencies = serviceDependencies == null ? new ArrayList<>() : serviceDependencies;
    }

    public List<String> getApiFlow() {
        return apiFlow;
    }

    public void setApiFlow(List<String> apiFlow) {
        this.apiFlow = apiFlow == null ? new ArrayList<>() : apiFlow;
    }

    public List<String> getComponentRelationships() {
        return componentRelationships;
    }

    public void setComponentRelationships(List<String> componentRelationships) {
        this.componentRelationships = componentRelationships == null ? new ArrayList<>() : componentRelationships;
    }

    public List<String> getSuggestedImprovements() {
        return suggestedImprovements;
    }

    public void setSuggestedImprovements(List<String> suggestedImprovements) {
        this.suggestedImprovements = suggestedImprovements == null ? new ArrayList<>() : suggestedImprovements;
    }

    public List<String> getSecurityRecommendations() {
        return securityRecommendations;
    }

    public void setSecurityRecommendations(List<String> securityRecommendations) {
        this.securityRecommendations = securityRecommendations == null ? new ArrayList<>() : securityRecommendations;
    }

    public String getSecuritySummary() {
        return securitySummary;
    }

    public void setSecuritySummary(String securitySummary) {
        this.securitySummary = securitySummary;
    }

    public List<String> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(List<String> vulnerabilities) {
        this.vulnerabilities = vulnerabilities == null ? new ArrayList<>() : vulnerabilities;
    }

    public List<String> getOwaspTop10() {
        return owaspTop10;
    }

    public void setOwaspTop10(List<String> owaspTop10) {
        this.owaspTop10 = owaspTop10 == null ? new ArrayList<>() : owaspTop10;
    }

    public List<String> getAuthenticationIssues() {
        return authenticationIssues;
    }

    public void setAuthenticationIssues(List<String> authenticationIssues) {
        this.authenticationIssues = authenticationIssues == null ? new ArrayList<>() : authenticationIssues;
    }

    public List<String> getAuthorizationIssues() {
        return authorizationIssues;
    }

    public void setAuthorizationIssues(List<String> authorizationIssues) {
        this.authorizationIssues = authorizationIssues == null ? new ArrayList<>() : authorizationIssues;
    }

    public List<String> getJwtRecommendations() {
        return jwtRecommendations;
    }

    public void setJwtRecommendations(List<String> jwtRecommendations) {
        this.jwtRecommendations = jwtRecommendations == null ? new ArrayList<>() : jwtRecommendations;
    }

    public List<String> getCsrfRecommendations() {
        return csrfRecommendations;
    }

    public void setCsrfRecommendations(List<String> csrfRecommendations) {
        this.csrfRecommendations = csrfRecommendations == null ? new ArrayList<>() : csrfRecommendations;
    }

    public List<String> getXssRecommendations() {
        return xssRecommendations;
    }

    public void setXssRecommendations(List<String> xssRecommendations) {
        this.xssRecommendations = xssRecommendations == null ? new ArrayList<>() : xssRecommendations;
    }

    public List<String> getSqlInjectionRisks() {
        return sqlInjectionRisks;
    }

    public void setSqlInjectionRisks(List<String> sqlInjectionRisks) {
        this.sqlInjectionRisks = sqlInjectionRisks == null ? new ArrayList<>() : sqlInjectionRisks;
    }

    public List<String> getSecretsFound() {
        return secretsFound;
    }

    public void setSecretsFound(List<String> secretsFound) {
        this.secretsFound = secretsFound == null ? new ArrayList<>() : secretsFound;
    }

    public List<String> getDependencyRisks() {
        return dependencyRisks;
    }

    public void setDependencyRisks(List<String> dependencyRisks) {
        this.dependencyRisks = dependencyRisks == null ? new ArrayList<>() : dependencyRisks;
    }

    public List<String> getInsecureConfigurations() {
        return insecureConfigurations;
    }

    public void setInsecureConfigurations(List<String> insecureConfigurations) {
        this.insecureConfigurations = insecureConfigurations == null ? new ArrayList<>() : insecureConfigurations;
    }

    public List<String> getEncryptionRecommendations() {
        return encryptionRecommendations;
    }

    public void setEncryptionRecommendations(List<String> encryptionRecommendations) {
        this.encryptionRecommendations = encryptionRecommendations == null ? new ArrayList<>() : encryptionRecommendations;
    }

    public List<String> getInputValidationRecommendations() {
        return inputValidationRecommendations;
    }

    public void setInputValidationRecommendations(List<String> inputValidationRecommendations) {
        this.inputValidationRecommendations = inputValidationRecommendations == null ? new ArrayList<>() : inputValidationRecommendations;
    }

    public List<String> getSecurityHeaders() {
        return securityHeaders;
    }

    public void setSecurityHeaders(List<String> securityHeaders) {
        this.securityHeaders = securityHeaders == null ? new ArrayList<>() : securityHeaders;
    }

    public List<String> getRateLimitingRecommendations() {
        return rateLimitingRecommendations;
    }

    public void setRateLimitingRecommendations(List<String> rateLimitingRecommendations) {
        this.rateLimitingRecommendations = rateLimitingRecommendations == null ? new ArrayList<>() : rateLimitingRecommendations;
    }

    public List<String> getLoggingAuditRecommendations() {
        return loggingAuditRecommendations;
    }

    public void setLoggingAuditRecommendations(List<String> loggingAuditRecommendations) {
        this.loggingAuditRecommendations = loggingAuditRecommendations == null ? new ArrayList<>() : loggingAuditRecommendations;
    }

    public List<String> getComplianceRecommendations() {
        return complianceRecommendations;
    }

    public void setComplianceRecommendations(List<String> complianceRecommendations) {
        this.complianceRecommendations = complianceRecommendations == null ? new ArrayList<>() : complianceRecommendations;
    }

    public String getGeneratedSecurityChecklist() {
        return generatedSecurityChecklist;
    }

    public void setGeneratedSecurityChecklist(String generatedSecurityChecklist) {
        this.generatedSecurityChecklist = generatedSecurityChecklist;
    }

    public String getGeneratedThreatModel() {
        return generatedThreatModel;
    }

    public void setGeneratedThreatModel(String generatedThreatModel) {
        this.generatedThreatModel = generatedThreatModel;
    }

    public String getGeneratedSecurityMarkdown() {
        return generatedSecurityMarkdown;
    }

    public void setGeneratedSecurityMarkdown(String generatedSecurityMarkdown) {
        this.generatedSecurityMarkdown = generatedSecurityMarkdown;
    }

    public String getDatabaseSummary() {
        return databaseSummary;
    }

    public void setDatabaseSummary(String databaseSummary) {
        this.databaseSummary = databaseSummary;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities == null ? new ArrayList<>() : entities;
    }

    public List<String> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<String> relationships) {
        this.relationships = relationships == null ? new ArrayList<>() : relationships;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys == null ? new ArrayList<>() : primaryKeys;
    }

    public List<String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<String> foreignKeys) {
        this.foreignKeys = foreignKeys == null ? new ArrayList<>() : foreignKeys;
    }

    public List<String> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<String> indexes) {
        this.indexes = indexes == null ? new ArrayList<>() : indexes;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints == null ? new ArrayList<>() : constraints;
    }

    public List<String> getNormalizationSuggestions() {
        return normalizationSuggestions;
    }

    public void setNormalizationSuggestions(List<String> normalizationSuggestions) {
        this.normalizationSuggestions = normalizationSuggestions == null ? new ArrayList<>() : normalizationSuggestions;
    }

    public List<String> getMissingTables() {
        return missingTables;
    }

    public void setMissingTables(List<String> missingTables) {
        this.missingTables = missingTables == null ? new ArrayList<>() : missingTables;
    }

    public List<String> getMigrationSuggestions() {
        return migrationSuggestions;
    }

    public void setMigrationSuggestions(List<String> migrationSuggestions) {
        this.migrationSuggestions = migrationSuggestions == null ? new ArrayList<>() : migrationSuggestions;
    }

    public List<String> getPostgresRecommendations() {
        return postgresRecommendations;
    }

    public void setPostgresRecommendations(List<String> postgresRecommendations) {
        this.postgresRecommendations = postgresRecommendations == null ? new ArrayList<>() : postgresRecommendations;
    }

    public String getGeneratedSqlSchema() {
        return generatedSqlSchema;
    }

    public void setGeneratedSqlSchema(String generatedSqlSchema) {
        this.generatedSqlSchema = generatedSqlSchema;
    }

    public String getGeneratedMermaidERDiagram() {
        return generatedMermaidERDiagram;
    }

    public void setGeneratedMermaidERDiagram(String generatedMermaidERDiagram) {
        this.generatedMermaidERDiagram = generatedMermaidERDiagram;
    }

    public String getApiSummary() {
        return apiSummary;
    }

    public void setApiSummary(String apiSummary) {
        this.apiSummary = apiSummary;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources == null ? new ArrayList<>() : resources;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints == null ? new ArrayList<>() : endpoints;
    }

    public List<String> getRequestDtos() {
        return requestDtos;
    }

    public void setRequestDtos(List<String> requestDtos) {
        this.requestDtos = requestDtos == null ? new ArrayList<>() : requestDtos;
    }

    public List<String> getResponseDtos() {
        return responseDtos;
    }

    public void setResponseDtos(List<String> responseDtos) {
        this.responseDtos = responseDtos == null ? new ArrayList<>() : responseDtos;
    }

    public List<String> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(List<String> validationRules) {
        this.validationRules = validationRules == null ? new ArrayList<>() : validationRules;
    }

    public List<String> getAuthenticationRequirements() {
        return authenticationRequirements;
    }

    public void setAuthenticationRequirements(List<String> authenticationRequirements) {
        this.authenticationRequirements = authenticationRequirements == null ? new ArrayList<>() : authenticationRequirements;
    }

    public List<String> getAuthorizationRequirements() {
        return authorizationRequirements;
    }

    public void setAuthorizationRequirements(List<String> authorizationRequirements) {
        this.authorizationRequirements = authorizationRequirements == null ? new ArrayList<>() : authorizationRequirements;
    }

    public List<String> getHttpStatusCodes() {
        return httpStatusCodes;
    }

    public void setHttpStatusCodes(List<String> httpStatusCodes) {
        this.httpStatusCodes = httpStatusCodes == null ? new ArrayList<>() : httpStatusCodes;
    }

    public List<String> getErrorResponses() {
        return errorResponses;
    }

    public void setErrorResponses(List<String> errorResponses) {
        this.errorResponses = errorResponses == null ? new ArrayList<>() : errorResponses;
    }

    public String getOpenApiSpec() {
        return openApiSpec;
    }

    public void setOpenApiSpec(String openApiSpec) {
        this.openApiSpec = openApiSpec;
    }

    public List<String> getCurlExamples() {
        return curlExamples;
    }

    public void setCurlExamples(List<String> curlExamples) {
        this.curlExamples = curlExamples == null ? new ArrayList<>() : curlExamples;
    }

    public List<String> getPostmanExamples() {
        return postmanExamples;
    }

    public void setPostmanExamples(List<String> postmanExamples) {
        this.postmanExamples = postmanExamples == null ? new ArrayList<>() : postmanExamples;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions == null ? new ArrayList<>() : improvementSuggestions;
    }

    public String getSystemSummary() {
        return systemSummary;
    }

    public void setSystemSummary(String systemSummary) {
        this.systemSummary = systemSummary;
    }

    public String getProjectOverview() {
        return projectOverview;
    }

    public void setProjectOverview(String projectOverview) {
        this.projectOverview = projectOverview;
    }

    public List<String> getFrontendArchitecture() {
        return frontendArchitecture;
    }

    public void setFrontendArchitecture(List<String> frontendArchitecture) {
        this.frontendArchitecture = frontendArchitecture == null ? new ArrayList<>() : frontendArchitecture;
    }

    public List<String> getBackendArchitecture() {
        return backendArchitecture;
    }

    public void setBackendArchitecture(List<String> backendArchitecture) {
        this.backendArchitecture = backendArchitecture == null ? new ArrayList<>() : backendArchitecture;
    }

    public List<String> getDatabaseArchitecture() {
        return databaseArchitecture;
    }

    public void setDatabaseArchitecture(List<String> databaseArchitecture) {
        this.databaseArchitecture = databaseArchitecture == null ? new ArrayList<>() : databaseArchitecture;
    }

    public List<String> getApiArchitecture() {
        return apiArchitecture;
    }

    public void setApiArchitecture(List<String> apiArchitecture) {
        this.apiArchitecture = apiArchitecture == null ? new ArrayList<>() : apiArchitecture;
    }

    public List<String> getServiceCommunication() {
        return serviceCommunication;
    }

    public void setServiceCommunication(List<String> serviceCommunication) {
        this.serviceCommunication = serviceCommunication == null ? new ArrayList<>() : serviceCommunication;
    }

    public List<String> getModuleDependencies() {
        return moduleDependencies;
    }

    public void setModuleDependencies(List<String> moduleDependencies) {
        this.moduleDependencies = moduleDependencies == null ? new ArrayList<>() : moduleDependencies;
    }

    public List<String> getDeploymentArchitecture() {
        return deploymentArchitecture;
    }

    public void setDeploymentArchitecture(List<String> deploymentArchitecture) {
        this.deploymentArchitecture = deploymentArchitecture == null ? new ArrayList<>() : deploymentArchitecture;
    }

    public List<String> getScalabilityRecommendations() {
        return scalabilityRecommendations;
    }

    public void setScalabilityRecommendations(List<String> scalabilityRecommendations) {
        this.scalabilityRecommendations = scalabilityRecommendations == null ? new ArrayList<>() : scalabilityRecommendations;
    }

    public List<String> getPerformanceRecommendations() {
        return performanceRecommendations;
    }

    public void setPerformanceRecommendations(List<String> performanceRecommendations) {
        this.performanceRecommendations = performanceRecommendations == null ? new ArrayList<>() : performanceRecommendations;
    }

    public String getCachingStrategy() {
        return cachingStrategy;
    }

    public void setCachingStrategy(String cachingStrategy) {
        this.cachingStrategy = cachingStrategy;
    }

    public String getLoadBalancingStrategy() {
        return loadBalancingStrategy;
    }

    public void setLoadBalancingStrategy(String loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    public List<String> getSecurityArchitecture() {
        return securityArchitecture;
    }

    public void setSecurityArchitecture(List<String> securityArchitecture) {
        this.securityArchitecture = securityArchitecture == null ? new ArrayList<>() : securityArchitecture;
    }

    public List<String> getAuthenticationFlow() {
        return authenticationFlow;
    }

    public void setAuthenticationFlow(List<String> authenticationFlow) {
        this.authenticationFlow = authenticationFlow == null ? new ArrayList<>() : authenticationFlow;
    }

    public List<String> getAuthorizationFlow() {
        return authorizationFlow;
    }

    public void setAuthorizationFlow(List<String> authorizationFlow) {
        this.authorizationFlow = authorizationFlow == null ? new ArrayList<>() : authorizationFlow;
    }

    public List<String> getMonitoringRecommendations() {
        return monitoringRecommendations;
    }

    public void setMonitoringRecommendations(List<String> monitoringRecommendations) {
        this.monitoringRecommendations = monitoringRecommendations == null ? new ArrayList<>() : monitoringRecommendations;
    }

    public List<String> getLoggingRecommendations() {
        return loggingRecommendations;
    }

    public void setLoggingRecommendations(List<String> loggingRecommendations) {
        this.loggingRecommendations = loggingRecommendations == null ? new ArrayList<>() : loggingRecommendations;
    }

    public List<String> getDisasterRecoveryRecommendations() {
        return disasterRecoveryRecommendations;
    }

    public void setDisasterRecoveryRecommendations(List<String> disasterRecoveryRecommendations) {
        this.disasterRecoveryRecommendations = disasterRecoveryRecommendations == null ? new ArrayList<>() : disasterRecoveryRecommendations;
    }

    public List<String> getTechnologyRecommendations() {
        return technologyRecommendations;
    }

    public void setTechnologyRecommendations(List<String> technologyRecommendations) {
        this.technologyRecommendations = technologyRecommendations == null ? new ArrayList<>() : technologyRecommendations;
    }

    public List<String> getFutureImprovements() {
        return futureImprovements;
    }

    public void setFutureImprovements(List<String> futureImprovements) {
        this.futureImprovements = futureImprovements == null ? new ArrayList<>() : futureImprovements;
    }

    public String getGeneratedSystemDesignMarkdown() {
        return generatedSystemDesignMarkdown;
    }

    public void setGeneratedSystemDesignMarkdown(String generatedSystemDesignMarkdown) {
        this.generatedSystemDesignMarkdown = generatedSystemDesignMarkdown;
    }

    public String getGeneratedMermaidFlowDiagram() {
        return generatedMermaidFlowDiagram;
    }

    public void setGeneratedMermaidFlowDiagram(String generatedMermaidFlowDiagram) {
        this.generatedMermaidFlowDiagram = generatedMermaidFlowDiagram;
    }

    public String getDeploymentSummary() {
        return deploymentSummary;
    }

    public void setDeploymentSummary(String deploymentSummary) {
        this.deploymentSummary = deploymentSummary;
    }

    public List<String> getDockerRecommendations() {
        return dockerRecommendations;
    }

    public void setDockerRecommendations(List<String> dockerRecommendations) {
        this.dockerRecommendations = dockerRecommendations == null ? new ArrayList<>() : dockerRecommendations;
    }

    public String getDockerfile() {
        return dockerfile;
    }

    public void setDockerfile(String dockerfile) {
        this.dockerfile = dockerfile;
    }

    public String getDockerCompose() {
        return dockerCompose;
    }

    public void setDockerCompose(String dockerCompose) {
        this.dockerCompose = dockerCompose;
    }

    public String getKubernetesManifest() {
        return kubernetesManifest;
    }

    public void setKubernetesManifest(String kubernetesManifest) {
        this.kubernetesManifest = kubernetesManifest;
    }

    public String getGithubActionsWorkflow() {
        return githubActionsWorkflow;
    }

    public void setGithubActionsWorkflow(String githubActionsWorkflow) {
        this.githubActionsWorkflow = githubActionsWorkflow;
    }

    public List<String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(List<String> environmentVariables) {
        this.environmentVariables = environmentVariables == null ? new ArrayList<>() : environmentVariables;
    }

    public List<String> getSecretsRequired() {
        return secretsRequired;
    }

    public void setSecretsRequired(List<String> secretsRequired) {
        this.secretsRequired = secretsRequired == null ? new ArrayList<>() : secretsRequired;
    }

    public List<String> getDeploymentSteps() {
        return deploymentSteps;
    }

    public void setDeploymentSteps(List<String> deploymentSteps) {
        this.deploymentSteps = deploymentSteps == null ? new ArrayList<>() : deploymentSteps;
    }

    public List<String> getCiCdFlow() {
        return ciCdFlow;
    }

    public void setCiCdFlow(List<String> ciCdFlow) {
        this.ciCdFlow = ciCdFlow == null ? new ArrayList<>() : ciCdFlow;
    }

    public List<String> getScalingRecommendations() {
        return scalingRecommendations;
    }

    public void setScalingRecommendations(List<String> scalingRecommendations) {
        this.scalingRecommendations = scalingRecommendations == null ? new ArrayList<>() : scalingRecommendations;
    }

    public String getBackupStrategy() {
        return backupStrategy;
    }

    public void setBackupStrategy(String backupStrategy) {
        this.backupStrategy = backupStrategy;
    }

    public String getRollbackStrategy() {
        return rollbackStrategy;
    }

    public void setRollbackStrategy(String rollbackStrategy) {
        this.rollbackStrategy = rollbackStrategy;
    }

    public List<String> getCloudRecommendations() {
        return cloudRecommendations;
    }

    public void setCloudRecommendations(List<String> cloudRecommendations) {
        this.cloudRecommendations = cloudRecommendations == null ? new ArrayList<>() : cloudRecommendations;
    }

    public String getEstimatedDeploymentCost() {
        return estimatedDeploymentCost;
    }

    public void setEstimatedDeploymentCost(String estimatedDeploymentCost) {
        this.estimatedDeploymentCost = estimatedDeploymentCost;
    }

    public String getGeneratedMarkdown() {
        return generatedMarkdown;
    }

    public void setGeneratedMarkdown(String generatedMarkdown) {
        this.generatedMarkdown = generatedMarkdown;
    }

    public List<ReviewSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<ReviewSuggestion> suggestions) {
        this.suggestions = suggestions == null ? new ArrayList<>() : suggestions;
    }

    public static class ReviewSuggestion {
        private String id;
        private String severity;
        private String title;
        private String file;
        private String description;
        private String suggestedFix;
        private String line;
        private String lineNumber;
        private Double confidenceScore;

        public ReviewSuggestion() {
        }

        public ReviewSuggestion(String id, String severity, String title, String file, String description, String suggestedFix, String line) {
            this.id = id;
            this.severity = severity;
            this.title = title;
            this.file = file;
            this.description = description;
            this.suggestedFix = suggestedFix;
            this.line = line;
        }

        public ReviewSuggestion(String id, String severity, String title, String file, String description, String suggestedFix, String line, String lineNumber, Double confidenceScore) {
            this.id = id;
            this.severity = severity;
            this.title = title;
            this.file = file;
            this.description = description;
            this.suggestedFix = suggestedFix;
            this.line = line;
            this.lineNumber = lineNumber;
            this.confidenceScore = confidenceScore;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSuggestedFix() {
            return suggestedFix;
        }

        public void setSuggestedFix(String suggestedFix) {
            this.suggestedFix = suggestedFix;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(String lineNumber) {
            this.lineNumber = lineNumber;
        }

        public Double getConfidenceScore() {
            return confidenceScore;
        }

        public void setConfidenceScore(Double confidenceScore) {
            this.confidenceScore = confidenceScore;
        }
    }
}
