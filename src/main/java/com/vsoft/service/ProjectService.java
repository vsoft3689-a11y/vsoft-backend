package com.vsoft.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.vsoft.entity.Project;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.vsoft.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getFilteredProjects(String degree, String branch, String type, String domain) {
        return projectRepository.findByDegreeAndBranchAndTypeAndDomain(degree, branch, type, domain);
    }

    public Project createProject(Project project) {
        if (projectRepository.existsByTitle(project.getTitle())) {
            throw new IllegalArgumentException("Project with name '" + project.getTitle() + "' already exists");
        }
        return projectRepository.save(project);
    }

    public List<Project> saveAllProjects(List<Project> projects) {
        projects.forEach(project -> {
            if (projectRepository.existsByTitle(project.getTitle())) {
                throw new IllegalArgumentException("Project with name '" + project.getTitle() + "' already exists");
            }
        });
        return projectRepository.saveAll(projects);
    }

    public Map<String, Object> saveFileData(InputStream file) throws IOException {
        List<Project> projectsToSave = new LinkedList<>();
        List<String> uniqueTitles = new ArrayList<>();
        List<String> duplicateTitles = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Fetch existing titles once (avoid 1 query per row)
            Set<String> existingTitles = new HashSet<>(projectRepository.findAllTitles());

            sheet.forEach(row -> {
                if (row.getRowNum() == 0) return; // Skip header

                if (row.getPhysicalNumberOfCells() < 6) {
                    throw new IllegalArgumentException(
                            "Invalid file format at row " + row.getRowNum() + " (less columns)"
                    );
                }

                String title = row.getCell(4) != null ? row.getCell(4).toString().trim() : "";
                if (existingTitles.contains(title)) {
                    duplicateTitles.add("(row " + row.getRowNum() + ")");
                } else {
                    Project p = new Project();
                    p.setDegree(row.getCell(0).getStringCellValue());
                    p.setBranch(row.getCell(1).getStringCellValue());
                    p.setType(row.getCell(2).getStringCellValue());
                    p.setDomain(row.getCell(3).getStringCellValue());
                    p.setTitle(title);
                    p.setDescription(row.getCell(5).getStringCellValue());
                    projectsToSave.add(p);
                    uniqueTitles.add("(row " + row.getRowNum() + ") ");
                }
            });

            // Save only new projects
            projectRepository.saveAll(projectsToSave);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("savedProjects", uniqueTitles.isEmpty() ? "No new projects to add" : uniqueTitles);
            response.put("duplicates", duplicateTitles.isEmpty() ? "No duplicate projects found" : duplicateTitles);
            return response;
        }
    }

}
