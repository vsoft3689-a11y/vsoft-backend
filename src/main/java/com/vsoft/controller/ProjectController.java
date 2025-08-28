package com.vsoft.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vsoft.entity.Project;
import com.vsoft.service.ProjectService;

@CrossOrigin(origins = {"https://sathvikagundapu.github.io","http://127.0.0.1:5500","http://localhost:5500"},allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects/{degree}/{branch}/{type}/{domain}")
    public List<Project> getFilteredProjects(@PathVariable @NotBlank(message = "Degree is required") String degree,
                                             @PathVariable @NotBlank(message = "Degree is required") String branch,
                                             @PathVariable @Pattern(regexp = "Major|Mini", message = "Type must be either 'major' or 'mini'") String type,
                                             @PathVariable @NotBlank(message = "Domain is required") String domain) {
        return projectService.getFilteredProjects(degree, branch, type, domain);
    }

    @PostMapping("/project")
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        try {
            Project proj = projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(proj);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/projects/saveAll")
    public ResponseEntity<?> saveAllProjects(@Valid @RequestBody List<Project> projects) {
        try {
            List<Project> saved = projectService.saveAllProjects(projects);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/projects/upload")
    public ResponseEntity<?> saveFileData(@RequestParam("file") @NotNull(message = "File is required") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file is empty"));
            }
            Map<String, Object> response = projectService.saveFileData(file.getInputStream());
//            if (!projects.isEmpty()) {
//                return ResponseEntity.ok(Map.of("message", "Data added successfully", "recordsSaved", projects.size()));
//            } else {
//                return ResponseEntity.badRequest().body(Map.of("error", "No data saved"));
//            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Server error while reading file"));
        }
    }
}
