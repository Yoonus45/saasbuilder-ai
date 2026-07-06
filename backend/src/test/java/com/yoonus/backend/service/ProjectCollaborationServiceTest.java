package com.yoonus.backend.service;

import com.yoonus.backend.dto.ProjectMemberResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.WorkspaceRole;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.repository.WorkspaceInvitationRepository;
import com.yoonus.backend.repository.WorkspaceMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectCollaborationServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceInvitationRepository workspaceInvitationRepository;

    @Test
    void getMembersCreatesOwnerMembershipForExistingProjects() {
        Project project = new Project();
        project.setId(7L);
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setName("Owner");

        when(projectRepository.findById(7L)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.findByProject(project)).thenReturn(List.of());
        when(workspaceMemberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectCollaborationService service = new ProjectCollaborationService(
                projectRepository,
                userRepository,
                workspaceMemberRepository,
                workspaceInvitationRepository
        );

        List<ProjectMemberResponse> members = service.getMembers(7L, "owner@example.com");

        assertEquals(1, members.size());
        assertEquals(WorkspaceRole.OWNER, members.get(0).getRole());
        verify(workspaceMemberRepository).save(any());
    }
}
