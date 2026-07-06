package com.yoonus.backend.service.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoonus.backend.dto.github.GithubConnectResponse;
import com.yoonus.backend.dto.github.GithubProfileResponse;
import com.yoonus.backend.dto.github.GithubRepoResponse;
import com.yoonus.backend.entity.GeneratedFile;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.github.GithubConnection;
import com.yoonus.backend.repository.GeneratedFileRepository;
import com.yoonus.backend.repository.GithubConnectionRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GithubService {

    private final GithubConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final GeneratedFileRepository generatedFileRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${github.client-id:}")
    private String clientId;

    @Value("${github.client-secret:}")
    private String clientSecret;

    @Value("${github.redirect-uri:http://localhost:8080/api/github/callback}")
    private String redirectUri;

    public GithubService(GithubConnectionRepository connectionRepository,
                         UserRepository userRepository,
                         ProjectRepository projectRepository,
                         GeneratedFileRepository generatedFileRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.generatedFileRepository = generatedFileRepository;
    }

    public GithubConnectResponse getConnectUrl(String email) {
        String scope = "repo,read:user";
        String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String authUrl = "https://github.com/login/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + encodedRedirect + "&scope=" + scope;
        return new GithubConnectResponse(authUrl, "Connect GitHub");
    }

    public GithubProfileResponse completeOAuth(String code, String email) {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            throw new IllegalStateException("GitHub OAuth is not configured");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String tokenUrl = "https://github.com/login/oauth/access_token";
        Map<String, String> body = Map.of("client_id", clientId, "client_secret", clientSecret, "code", code, "redirect_uri", redirectUri);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, JsonNode.class);
        String accessToken = response.getBody().path("access_token").asText();
        if (accessToken.isBlank()) {
            throw new IllegalStateException("GitHub authentication failed");
        }

        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);
        ResponseEntity<JsonNode> profileResponse = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, profileRequest, JsonNode.class);
        JsonNode profile = profileResponse.getBody();

        GithubConnection connection = connectionRepository.findByUser(user).orElse(new GithubConnection());
        connection.setUser(user);
        connection.setAccessToken(accessToken);
        connection.setUsername(profile.path("login").asText());
        connection.setAvatarUrl(profile.path("avatar_url").asText());
        connectionRepository.save(connection);

        return new GithubProfileResponse(connection.getUsername(), connection.getAvatarUrl(), connection.getConnectedAt());
    }

    public GithubProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        GithubConnection connection = connectionRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("GitHub account not connected"));
        return new GithubProfileResponse(connection.getUsername(), connection.getAvatarUrl(), connection.getConnectedAt());
    }

    public List<GithubRepoResponse> listRepos(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        GithubConnection connection = connectionRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("GitHub account not connected"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(connection.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<JsonNode[]> response = restTemplate.exchange("https://api.github.com/user/repos?per_page=20", HttpMethod.GET, request, JsonNode[].class);
        List<GithubRepoResponse> repos = new ArrayList<>();
        for (JsonNode node : response.getBody()) {
            repos.add(new GithubRepoResponse(node.path("name").asText(), node.path("full_name").asText(), node.path("description").asText(), node.path("default_branch").asText(), node.path("html_url").asText()));
        }
        return repos;
    }

    public Project importRepo(String email, String repoFullName, String branch) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        GithubConnection connection = connectionRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("GitHub account not connected"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(connection.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = "https://api.github.com/repos/" + repoFullName + "/git/trees/" + branch + "?recursive=1";
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class);
        JsonNode tree = response.getBody().path("tree");
        Project project = new Project();
        project.setTitle(repoFullName);
        project.setDescription("Imported from GitHub repository " + repoFullName);
        project.setPrompt("Imported from GitHub");
        project.setFramework("React");
        project.setStatus(com.yoonus.backend.entity.ProjectStatus.COMPLETED);
        project.setUser(user);
        project = projectRepository.save(project);

        List<GeneratedFile> files = new ArrayList<>();
        if (tree.isArray()) {
            for (JsonNode node : tree) {
                String path = node.path("path").asText();
                String type = node.path("type").asText();
                if ("blob".equals(type) && !path.isBlank()) {
                    String downloadUrl = "https://raw.githubusercontent.com/" + repoFullName + "/" + branch + "/" + path;
                    String content = restTemplate.getForObject(downloadUrl, String.class);
                    GeneratedFile generatedFile = new GeneratedFile();
                    generatedFile.setFilename(path.substring(path.lastIndexOf('/') + 1));
                    generatedFile.setFilepath(path);
                    generatedFile.setLanguage(path.substring(path.lastIndexOf('.') + 1));
                    generatedFile.setCode(content == null ? "" : content);
                    generatedFile.setProject(project);
                    files.add(generatedFile);
                }
            }
        }
        generatedFileRepository.saveAll(files);
        return project;
    }

    public String pushProject(String email, Long projectId, String commitMessage) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        GithubConnection connection = connectionRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("GitHub account not connected"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        List<GeneratedFile> files = generatedFileRepository.findByProjectId(projectId);
        StringBuilder tree = new StringBuilder();
        for (GeneratedFile file : files) {
            tree.append(file.getFilepath()).append("\n");
        }
        return "GitHub push is simulated for project " + project.getTitle() + " with " + files.size() + " files. Commit: " + commitMessage + "\n" + tree;
    }

    public String createCommit(String email, Long projectId, String message) {
        return pushProject(email, projectId, message);
    }

    public String createPullRequest(String email, Long projectId, String branch, String title) {
        return "PR creation simulated for project " + projectId + " on branch " + branch + " with title " + title;
    }

    public List<String> getHistory(String email) {
        return List.of("Imported repository", "Pushed workspace");
    }
}
