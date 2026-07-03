package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.FileExportResponse;
import com.yoonus.backend.entity.Project;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.exception.UnauthorizedAccessException;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.FileExportService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileExportServiceImpl implements FileExportService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public FileExportServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FileExportResponse exportProjectAsZip(Long projectId, String email) {
        Project project = getProjectForUser(projectId, email);
        
        try {
            byte[] zipData = createProjectZip(project);
            String fileName = sanitizeFileName(project.getTitle()) + ".zip";
            
            return new FileExportResponse(
                    "/api/projects/" + projectId + "/download",
                    fileName,
                    zipData.length,
                    "Project exported successfully"
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP file", e);
        }
    }

    @Override
    public byte[] exportProjectCode(Long projectId, String email) {
        Project project = getProjectForUser(projectId, email);
        String code = project.getGeneratedCode() != null ? project.getGeneratedCode() : "";
        return code.getBytes();
    }

    private byte[] createProjectZip(Project project) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        // Add README
        String readmeContent = generateReadme(project);
        addFileToZip(zos, "README.md", readmeContent.getBytes());

        // Add generated code
        String codeFileName = "index." + getFileExtension(project.getFramework());
        if (project.getGeneratedCode() != null && !project.getGeneratedCode().isEmpty()) {
            addFileToZip(zos, codeFileName, project.getGeneratedCode().getBytes());
        }

        // Add package.json for frameworks that need it
        if (isJavaScriptFramework(project.getFramework())) {
            String packageJson = generatePackageJson(project);
            addFileToZip(zos, "package.json", packageJson.getBytes());
        }

        // Add metadata
        String metadata = generateMetadata(project);
        addFileToZip(zos, "metadata.json", metadata.getBytes());

        zos.close();
        return baos.toByteArray();
    }

    private void addFileToZip(ZipOutputStream zos, String fileName, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        entry.setSize(content.length);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }

    private String generateReadme(Project project) {
        return String.format(
                "# %s\n\n%s\n\n## Framework\n%s\n\n## Description\n%s\n\n## Generated\n%s\n\n" +
                "## Setup\n\n```bash\nnpm install\nnpm run dev\n```\n",
                project.getTitle(),
                project.getPrompt(),
                project.getFramework(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }

    private String generatePackageJson(Project project) {
        return String.format(
                "{\n" +
                "  \"name\": \"%s\",\n" +
                "  \"version\": \"0.0.1\",\n" +
                "  \"type\": \"module\",\n" +
                "  \"scripts\": {\n" +
                "    \"dev\": \"vite\",\n" +
                "    \"build\": \"tsc && vite build\"\n" +
                "  },\n" +
                "  \"dependencies\": {\n" +
                "    \"react\": \"^18.3.1\",\n" +
                "    \"react-dom\": \"^18.3.1\"\n" +
                "  },\n" +
                "  \"devDependencies\": {\n" +
                "    \"@types/react\": \"^18.3.4\",\n" +
                "    \"typescript\": \"^5.6.2\"\n" +
                "  }\n" +
                "}\n",
                sanitizeFileName(project.getTitle()).replace("-", "")
        );
    }

    private String generateMetadata(Project project) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "{\n" +
                "  \"title\": \"%s\",\n" +
                "  \"description\": \"%s\",\n" +
                "  \"framework\": \"%s\",\n" +
                "  \"status\": \"%s\",\n" +
                "  \"createdAt\": \"%s\",\n" +
                "  \"updatedAt\": \"%s\"\n" +
                "}\n",
                escapeJson(project.getTitle()),
                escapeJson(project.getDescription()),
                project.getFramework(),
                project.getStatus(),
                formatter.format(project.getCreatedAt()),
                formatter.format(project.getUpdatedAt())
        );
    }

    private boolean isJavaScriptFramework(String framework) {
        if (framework == null) return false;
        String lower = framework.toLowerCase();
        return lower.contains("react") || lower.contains("vue") || lower.contains("angular") || 
               lower.contains("svelte") || lower.contains("node");
    }

    private String getFileExtension(String framework) {
        if (framework == null) return "js";
        String lower = framework.toLowerCase();
        if (lower.contains("typescript") || lower.contains("ts")) return "ts";
        if (lower.contains("jsx") || lower.contains("react")) return "jsx";
        return "js";
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\-_]", "-").toLowerCase();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private Project getProjectForUser(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return projectRepository.findByIdAndUser(projectId, user)
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this project"));
    }
}
