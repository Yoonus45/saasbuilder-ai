package com.yoonus.backend.service;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.entity.GenerationJob;
import com.yoonus.backend.entity.JobStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
import com.yoonus.backend.repository.GenerationJobRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import com.yoonus.backend.service.ProjectVersionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerationJobServiceTest {

    @Mock
    private GenerationJobRepository generationJobRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiGenerationHistoryRepository generationHistoryRepository;

    @Mock
    private AiCodeGenerator aiCodeGenerator;

    @Test
    void createJobPersistsQueuedJobAndReturnsResponse() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        GenerationJob savedJob = new GenerationJob();
        savedJob.setId(42L);
        savedJob.setStatus(JobStatus.QUEUED);
        when(generationJobRepository.save(any(GenerationJob.class))).thenReturn(savedJob);

        GenerationJobService service = new GenerationJobService(
                generationJobRepository,
                projectRepository,
                userRepository,
                generationHistoryRepository,
                aiCodeGenerator,
                new SyncTaskExecutor(),
                new ProjectVersionService(projectRepository, null)
        );

        AiGenerationRequest request = new AiGenerationRequest();
        request.setTitle("My app");
        request.setDescription("Sample app");
        request.setPrompt("Build an app");
        request.setFramework("React");

        var response = service.createJob("user@example.com", request);

        assertNotNull(response);
        assertEquals(42L, response.getId());
        assertEquals(JobStatus.QUEUED, response.getStatus());
        verify(generationJobRepository).save(any(GenerationJob.class));
    }
}
