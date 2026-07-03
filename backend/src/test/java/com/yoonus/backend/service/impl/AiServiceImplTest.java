package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiCodeGenerator aiCodeGenerator;

    @InjectMocks
    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        AiGenerationResponse response = aiService.generateCode("owner@example.com", request);

        assertNotNull(response);
        assertEquals("AI App", response.getTitle());
        assertEquals(ProjectStatus.COMPLETED, response.getStatus());
        assertEquals("export default function App() {};", response.getGeneratedCode());
        verify(projectRepository).save(any(Project.class));
    }
}
