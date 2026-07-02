package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.AiGenerationResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.AiService;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiServiceImpl implements AiService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AiCodeGenerator aiCodeGenerator;

    public AiServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, AiCodeGenerator aiCodeGenerator) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.aiCodeGenerator = aiCodeGenerator;
    }

    @Override
    public AiGenerationResponse generateCode(String email, AiGenerationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        String generatedCode = aiCodeGenerator.generateCode(request.getPrompt(), request.getFramework());

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setPrompt(request.getPrompt());
        project.setGeneratedCode(generatedCode);
        project.setFramework(request.getFramework());
        project.setStatus(ProjectStatus.GENERATING);
        project.setUser(user);

        Project savedProject = projectRepository.save(project);

        return new AiGenerationResponse(
                savedProject.getId(),
                savedProject.getTitle(),
                "Code generated successfully",
                savedProject.getStatus(),
                savedProject.getGeneratedCode()
        );
    }
}
