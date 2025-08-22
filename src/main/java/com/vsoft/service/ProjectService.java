package com.vsoft.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

    public List<Project> saveFileData(InputStream file) throws IOException {
        List<Project> projects = new LinkedList<>();

        try (Workbook workbook = WorkbookFactory.create(file)) {  // Auto-close workbook
            Sheet sheet = workbook.getSheetAt(0);

            sheet.forEach(row -> {
                if (row.getRowNum() != 0) { // Skip header
                    if (row.getPhysicalNumberOfCells() < 6) {
                        throw new IllegalArgumentException(
                                "Invalid file format at row " + row.getRowNum() + " (less columns)"
                        );
                    }

                    Project p = new Project();
                    p.setDegree(row.getCell(0).getStringCellValue());
                    p.setBranch(row.getCell(1).getStringCellValue());
                    p.setType(row.getCell(2).getStringCellValue());
                    p.setDomain(row.getCell(3).getStringCellValue());
                    p.setTitle(row.getCell(4).getStringCellValue());
                    p.setDescription(row.getCell(5).getStringCellValue());

                    // Validate unique title
                    if (projectRepository.existsByTitle(p.getTitle())) {
                        throw new IllegalArgumentException(
                                "Project with title '" + p.getTitle() + "' already exists (row " + row.getRowNum() + ")"
                        );
                    }
                    projects.add(p);
                }
            });
            return projectRepository.saveAll(projects);
        }
    }
}
