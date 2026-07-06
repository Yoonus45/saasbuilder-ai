package com.yoonus.backend.service;

import com.yoonus.backend.dto.AiGenerationRequest;
import com.yoonus.backend.dto.GenerationJobResponse;
import com.yoonus.backend.entity.AiGenerationHistory;
import com.yoonus.backend.entity.GenerationJob;
import com.yoonus.backend.entity.JobStatus;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.ProjectStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
import com.yoonus.backend.repository.GenerationJobRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.impl.ai.AiCodeGenerator;
import com.yoonus.backend.service.ProjectVersionService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenerationJobService {

    private final GenerationJobRepository generationJobRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AiGenerationHistoryRepository generationHistoryRepository;
    private final AiCodeGenerator aiCodeGenerator;
    private final TaskExecutor taskExecutor;
    private final ProjectVersionService projectVersionService;

    public GenerationJobService(GenerationJobRepository generationJobRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository,
                                AiGenerationHistoryRepository generationHistoryRepository,
                                AiCodeGenerator aiCodeGenerator,
                                TaskExecutor taskExecutor,
                                ProjectVersionService projectVersionService) {
        this.generationJobRepository = generationJobRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.generationHistoryRepository = generationHistoryRepository;
        this.aiCodeGenerator = aiCodeGenerator;
        this.taskExecutor = taskExecutor;
        this.projectVersionService = projectVersionService;
    }

    @Transactional
    public GenerationJobResponse createJob(String email, AiGenerationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GenerationJob job = new GenerationJob();
        job.setUser(user);
        job.setTitle(request.getTitle());
        job.setPrompt(request.getPrompt());
        job.setFramework(request.getFramework());
        job.setDescription(request.getDescription());
        job.setStatus(JobStatus.QUEUED);
        job.setProgress(0);
        job.setLogs("Job queued\n");

        GenerationJob savedJob = generationJobRepository.save(job);
        taskExecutor.execute(() -> processJob(savedJob.getId()));
        return mapToResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<GenerationJobResponse> getJobs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return generationJobRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GenerationJobResponse getJob(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        GenerationJob job = generationJobRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return mapToResponse(job);
    }

    @Transactional
    public GenerationJobResponse cancelJob(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        GenerationJob job = generationJobRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED || job.getStatus() == JobStatus.CANCELLED) {
            return mapToResponse(job);
        }
        job.setStatus(JobStatus.CANCELLED);
        job.setProgress(100);
        job.setErrorMessage("Job cancelled by user");
        job.setLogs((job.getLogs() == null ? "" : job.getLogs()) + "Job cancelled by user\n");
        return mapToResponse(generationJobRepository.save(job));
    }

    @Transactional
    public GenerationJobResponse retryJob(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        GenerationJob job = generationJobRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(JobStatus.QUEUED);
        job.setProgress(0);
        job.setResult(null);
        job.setErrorMessage(null);
        job.setLogs("Job queued\n");
        GenerationJob savedJob = generationJobRepository.save(job);
        taskExecutor.execute(() -> processJob(savedJob.getId()));
        return mapToResponse(savedJob);
    }

    private void processJob(Long jobId) {
        Optional<GenerationJob> optionalJob = generationJobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return;
        }

        GenerationJob job = optionalJob.get();
        if (job.getStatus() == JobStatus.CANCELLED) {
            return;
        }

        try {
            job.setStatus(JobStatus.RUNNING);
            job.setProgress(10);
            appendLog(job, "Starting generation...");
            generationJobRepository.save(job);

            String generatedCode = aiCodeGenerator.generateCode(job.getPrompt(), job.getFramework());
            if (generatedCode == null || generatedCode.isBlank()) {
                throw new IllegalStateException("The AI provider returned empty content");
            }

            job.setProgress(80);
            appendLog(job, "Generation complete. Saving project...");
            generationJobRepository.save(job);

            Project project = new Project();
            project.setTitle(job.getTitle());
            project.setDescription(job.getDescription());
            project.setPrompt(job.getPrompt());
            project.setGeneratedCode(generatedCode);
            project.setFramework(job.getFramework());
            project.setStatus(ProjectStatus.COMPLETED);
            project.setUser(job.getUser());
            Project savedProject = projectRepository.save(project);

            job.setProject(savedProject);
            job.setResult(generatedCode);
            job.setStatus(JobStatus.COMPLETED);
            job.setProgress(100);
            appendLog(job, "Project saved successfully");
            generationJobRepository.save(job);

            generationHistoryRepository.save(new AiGenerationHistory(job.getUser(), job.getTitle(), job.getPrompt(), generatedCode, job.getFramework()));
            projectVersionService.createVersion(savedProject.getId(), "Background generation completed", job.getPrompt(), generatedCode, job.getFramework(), ProjectStatus.COMPLETED);
        } catch (Exception ex) {
            job.setStatus(JobStatus.FAILED);
            job.setProgress(100);
            job.setErrorMessage(ex.getMessage());
            appendLog(job, "Generation failed: " + ex.getMessage());
            generationJobRepository.save(job);
        }
    }

    private void appendLog(GenerationJob job, String message) {
        String existing = job.getLogs() == null ? "" : job.getLogs();
        job.setLogs(existing + message + "\n");
    }

    private GenerationJobResponse mapToResponse(GenerationJob job) {
        return new GenerationJobResponse(
                job.getId(),
                job.getTitle(),
                job.getPrompt(),
                job.getFramework(),
                job.getDescription(),
                job.getStatus(),
                job.getProgress(),
                job.getResult(),
                job.getErrorMessage(),
                job.getLogs(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
