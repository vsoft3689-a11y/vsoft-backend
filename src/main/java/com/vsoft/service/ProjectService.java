package com.vsoft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsoft.entity.Project;
import com.vsoft.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	public List<Project> getFilteredProjects(String degree, String branch, String type, String domain) {
		return projectRepository.findByDegreeAndBranchAndTypeAndDomain(degree, branch, type, domain);
	}

	public Project createProject(Project project) {
		return projectRepository.save(project);
	}

	public List<Project> saveAll(List<Project> projects) {
		return projectRepository.saveAll(projects);
	}

}
