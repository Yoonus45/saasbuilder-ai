package com.yoonus.backend.service;

import com.yoonus.backend.dto.ProjectVersionResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.ProjectVersion;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.ProjectVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectVersionServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectVersionRepository projectVersionRepository;

    @Test
    void createVersionIncrementsVersionNumberAndPersistsSummary() {
        Project project = new Project();
        project.setId(10L);
        project.setGenerationCount(2);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectVersionRepository.save(any(ProjectVersion.class))).thenAnswer(invocation -> {
            ProjectVersion version = invocation.getArgument(0);
            version.setId(99L);
            return version;
        });

        ProjectVersionService service = new ProjectVersionService(projectRepository, projectVersionRepository);

        ProjectVersionResponse response = service.createVersion(
                10L,
                "Refined layout",
                "Build a dashboard",
                "export default function App() {}",
                "React",
                ProjectStatus.COMPLETED
        );

        assertNotNull(response);
        assertEquals(3, response.getVersionNumber());
        assertEquals("Refined layout", response.getSummary());
        verify(projectVersionRepository).save(any(ProjectVersion.class));
    }
}
