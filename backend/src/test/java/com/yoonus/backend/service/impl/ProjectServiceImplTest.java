package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.CreateProjectRequest;
import com.yoonus.backend.dto.ProjectResponse;
import com.yoonus.backend.dto.UpdateProjectRequest;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.exception.UnauthorizedAccessException;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.repository.WorkspaceMemberRepository;
import com.yoonus.backend.service.WorkspaceCollaborationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceCollaborationService workspaceCollaborationService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProject_shouldPersistProjectForOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("AI Dashboard");
        request.setDescription("A sample dashboard");
        request.setPrompt("Create a dashboard");
        request.setFramework("React");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.createProject("owner@example.com", request);

        assertNotNull(response);
        assertEquals("AI Dashboard", response.getTitle());
        assertEquals("React", response.getFramework());
        assertEquals(ProjectStatus.CREATED, response.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createProject_shouldPersistProjectManagementFields() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("AI Dashboard");
        request.setDescription("A sample dashboard");
        request.setPrompt("Create a dashboard");
        request.setFramework("React");
        request.setFavorite(true);
        request.setPinned(true);
        request.setArchived(false);
        request.setTags("ai, dashboard");
        request.setGithubStatus("Connected");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.createProject("owner@example.com", request);

        assertNotNull(response);
        assertEquals(true, response.isFavorite());
        assertEquals(true, response.isPinned());
        assertEquals(false, response.isArchived());
        assertEquals("ai, dashboard", response.getTags());
        assertEquals("Connected", response.getGithubStatus());
        assertEquals(1, response.getGenerationCount());
    }

    @Test
    void updateProject_shouldThrowWhenProjectBelongsToAnotherUser() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        Project project = new Project();
        project.setId(10L);
        project.setTitle("Old title");
        project.setUser(otherUser);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findByIdAndUser(10L, owner)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedAccessException.class,
                () -> projectService.updateProject(10L, "owner@example.com", new UpdateProjectRequest()));
    }

    @Test
    void getProject_shouldReturnProjectWhenOwnedByUser() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        Project project = new Project();
        project.setId(12L);
        project.setTitle("My App");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setUser(owner);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findByIdAndUser(12L, owner)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProject(12L, "owner@example.com");

        assertEquals("My App", response.getTitle());
    }

    @Test
    void updateProject_shouldRejectViewerAccess() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        User viewer = new User();
        viewer.setId(2L);
        viewer.setEmail("viewer@example.com");

        Project project = new Project();
        project.setId(13L);
        project.setTitle("Shared App");
        project.setUser(owner);

        when(userRepository.findByEmail("viewer@example.com")).thenReturn(Optional.of(viewer));
        when(projectRepository.findById(13L)).thenReturn(Optional.of(project));
        when(workspaceCollaborationService.canView(project, viewer)).thenReturn(true);
        when(workspaceCollaborationService.canEdit(project, viewer)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class,
                () -> projectService.updateProject(13L, "viewer@example.com", new UpdateProjectRequest()));
    }

    @Test
    void deleteProject_shouldDeleteOwnedProject() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        Project project = new Project();
        project.setId(22L);
        project.setUser(owner);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findByIdAndUser(22L, owner)).thenReturn(Optional.of(project));

        projectService.deleteProject(22L, "owner@example.com");

        verify(projectRepository).delete(project);
    }

    @Test
    void getAllProjects_shouldReturnProjectsForOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");

        Project project = new Project();
        project.setId(21L);
        project.setTitle("Sample");
        project.setUser(owner);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(projectRepository.findAllByUser(owner)).thenReturn(List.of(project));

        List<ProjectResponse> projects = projectService.getAllProjects("owner@example.com");

        assertEquals(1, projects.size());
        assertEquals("Sample", projects.get(0).getTitle());
    }
}
