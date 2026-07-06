package com.yoonus.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "generated_files")
public class GeneratedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String filepath;
    private String language;
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}