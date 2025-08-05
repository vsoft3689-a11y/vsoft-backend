package com.vsoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsoft.entity.Project;
import com.vsoft.service.ProjectService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@GetMapping("/projects/{degree}/{branch}/{type}/{domain}")
	public List<Project> getFilteredProjects(@PathVariable String degree, @PathVariable String branch,
			@PathVariable String type, @PathVariable String domain) {
		return projectService.getFilteredProjects(degree, branch, type, domain);
	}

	@PostMapping("/project")
	public ResponseEntity<?> createProject(@RequestBody Project project) {
		Project proj = projectService.createProject(project);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(proj);
	}

	@PostMapping("/projects")
	public ResponseEntity<String> createProjects(@RequestBody List<Project> projects) {
		projectService.saveAll(projects);
		return ResponseEntity.status(HttpStatus.CREATED).body("Projects successfully added");
	}

}
