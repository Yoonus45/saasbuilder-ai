package com.yoonus.backend.controller.github;

import com.yoonus.backend.dto.github.GithubConnectResponse;
import com.yoonus.backend.dto.github.GithubProfileResponse;
import com.yoonus.backend.dto.github.GithubRepoResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.service.github.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/connect")
    public ResponseEntity<GithubConnectResponse> connect() {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.getConnectUrl(email));
    }

    @GetMapping("/callback")
    public ResponseEntity<GithubProfileResponse> callback(@RequestParam(required = false) String code) {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.completeOAuth(code, email));
    }

    @GetMapping("/me")
    public ResponseEntity<GithubProfileResponse> me() {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.getProfile(email));
    }

    @GetMapping("/repos")
    public ResponseEntity<List<GithubRepoResponse>> repos() {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.listRepos(email));
    }

    @PostMapping("/import")
    public ResponseEntity<Project> importRepo(@RequestBody Map<String, String> payload) {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.importRepo(email, payload.get("repoFullName"), payload.getOrDefault("branch", "main")));
    }

    @PostMapping("/push")
    public ResponseEntity<String> push(@RequestBody Map<String, Object> payload) {
        String email = currentUserEmail();
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        String commitMessage = payload.getOrDefault("commitMessage", "Update from workspace").toString();
        return ResponseEntity.ok(githubService.pushProject(email, projectId, commitMessage));
    }

    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestBody Map<String, Object> payload) {
        String email = currentUserEmail();
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        String message = payload.getOrDefault("message", "Update from workspace").toString();
        return ResponseEntity.ok(githubService.createCommit(email, projectId, message));
    }

    @PostMapping("/pull-request")
    public ResponseEntity<String> pullRequest(@RequestBody Map<String, Object> payload) {
        String email = currentUserEmail();
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        String branch = payload.getOrDefault("branch", "main").toString();
        String title = payload.getOrDefault("title", "Update from workspace").toString();
        return ResponseEntity.ok(githubService.createPullRequest(email, projectId, branch, title));
    }

    @GetMapping("/history")
    public ResponseEntity<List<String>> history() {
        String email = currentUserEmail();
        return ResponseEntity.ok(githubService.getHistory(email));
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
