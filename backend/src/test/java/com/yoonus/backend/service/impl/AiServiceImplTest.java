package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.dto.AiReviewRequest;
import com.yoonus.backend.dto.AiReviewResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
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
    private AiGenerationHistoryRepository generationHistoryRepository;

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
}
