package com.yoonus.backend.service;

import com.yoonus.backend.entity.Deployment;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.DeploymentRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public DeploymentService(DeploymentRepository deploymentRepository,
                             ProjectRepository projectRepository,
                             UserRepository userRepository) {
        this.deploymentRepository = deploymentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Deployment deployProject(String email, Long projectId, String provider) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Deployment deployment = new Deployment();
        deployment.setProject(project);
        deployment.setUser(user);
        deployment.setDeploymentId(UUID.randomUUID().toString().substring(0, 12));
        deployment.setProvider(provider);
        deployment.setStatus("Queued");
        deployment.setDeploymentUrl(null);

        return deploymentRepository.save(deployment);
    }

    public List<Deployment> getHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return deploymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Deployment updateDeploymentStatus(Long deploymentId, String status, String deploymentUrl) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new IllegalArgumentException("Deployment not found"));
        deployment.setStatus(status);
        if (deploymentUrl != null && !deploymentUrl.isBlank()) {
            deployment.setDeploymentUrl(deploymentUrl);
        }
        return deploymentRepository.save(deployment);
    }
}
