package com.yoonus.backend.controller;

import com.yoonus.backend.dto.InviteMemberRequest;
import com.yoonus.backend.dto.ProjectMemberResponse;
import com.yoonus.backend.dto.ProjectMemberUpdateRequest;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.service.WorkspaceCollaborationService;
import com.yoonus.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/collaboration")
public class WorkspaceCollaborationController {

    private final WorkspaceCollaborationService workspaceCollaborationService;
    private final UserService userService;

    public WorkspaceCollaborationController(WorkspaceCollaborationService workspaceCollaborationService, UserService userService) {
        this.workspaceCollaborationService = workspaceCollaborationService;
        this.userService = userService;
    }

    @GetMapping("/members")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable Long projectId, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        return ResponseEntity.ok(workspaceCollaborationService.listMembers(projectId, currentUser));
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<ProjectMemberResponse>> getInvitations(@PathVariable Long projectId, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        return ResponseEntity.ok(workspaceCollaborationService.listInvitations(projectId, currentUser));
    }

    @PostMapping("/members")
    public ResponseEntity<ProjectMemberResponse> inviteMember(@PathVariable Long projectId, @Valid @RequestBody InviteMemberRequest request, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceCollaborationService.inviteMember(projectId, request, currentUser));
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId, @RequestBody ProjectMemberUpdateRequest request, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        return ResponseEntity.ok(workspaceCollaborationService.updateMemberRole(projectId, memberId, request, currentUser));
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId, @PathVariable Long memberId, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        workspaceCollaborationService.removeMember(projectId, memberId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long invitationId, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        workspaceCollaborationService.acceptInvitation(invitationId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable Long invitationId, @RequestHeader("Authorization") String authHeader) {
        User currentUser = getCurrentUser(authHeader);
        workspaceCollaborationService.rejectInvitation(invitationId, currentUser);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return userService.getCurrentUserFromToken(token);
    }
}
