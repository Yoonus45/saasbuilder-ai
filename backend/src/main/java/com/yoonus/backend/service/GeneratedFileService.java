package com.yoonus.backend.service;

import com.yoonus.backend.entity.GeneratedFile;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.repository.GeneratedFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratedFileService {
    private final GeneratedFileRepository generatedFileRepository;

    public GeneratedFileService(GeneratedFileRepository generatedFileRepository) {
        this.generatedFileRepository = generatedFileRepository;
    }

    public void saveGeneratedFiles(Long projectId, List<GeneratedFile> files) {
        for (GeneratedFile file : files) {
            Project project = new Project();
            project.setId(projectId);
            file.setProject(project);
        }
        generatedFileRepository.saveAll(files);
    }

    public List<GeneratedFile> getFilesByProjectId(Long projectId) {
        return generatedFileRepository.findByProjectId(projectId);
    }
}
