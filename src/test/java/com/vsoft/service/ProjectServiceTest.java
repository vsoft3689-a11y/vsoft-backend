package com.vsoft.service;

import com.vsoft.entity.Project;
import com.vsoft.repository.ProjectRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetFilteredProjects(){
        Project project = new Project();
        project.setDegree("B.Tech");
        project.setBranch("CSE");
        project.setType("Major");
        project.setDomain("AI");

        when(projectRepository.findByDegreeAndBranchAndTypeAndDomain("B.Tech", "CSE", "Major", "AI"))
                .thenReturn(List.of(project));

        List<Project> projects = projectService.getFilteredProjects("B.Tech", "CSE", "Major", "AI");
        assertNotNull(projects);
        assertEquals("B.Tech", projects.get(0).getDegree());
        assertEquals("CSE", projects.get(0).getBranch());
        assertEquals("Major", projects.get(0).getType());
        assertEquals("AI", projects.get(0).getDomain());

        verify(projectRepository, times(1))
                .findByDegreeAndBranchAndTypeAndDomain("B.Tech", "CSE", "Major", "AI");
    }

    @Test
    void testCreateProject() {
        Project project = new Project();
        project.setTitle("Project1");

        when(projectRepository.existsByTitle("Project1")).thenReturn(false);
        when(projectRepository.save(project)).thenReturn(project);

        Project savedProject = projectService.createProject(project);

        assertNotNull(savedProject);
        assertEquals("Project1", savedProject.getTitle());
        verify(projectRepository, times(1)).existsByTitle("Project1");
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testSaveAllProjects(){
        Project project1 = new Project();
        project1.setTitle("Project1");
        Project project2 = new Project();
        project2.setTitle("Project2");

        List<Project> projects = List.of(project1, project2);

        when(projectRepository.existsByTitle("Project1")).thenReturn(false);
        when(projectRepository.existsByTitle("Project2")).thenReturn(false);
        when(projectRepository.saveAll(projects)).thenReturn(projects);

        List<Project> savedProjects = projectService.saveAllProjects(projects);

        assertNotNull(savedProjects);
        assertEquals(2, savedProjects.size());
        verify(projectRepository, times(1)).existsByTitle("Project1");
        verify(projectRepository, times(1)).existsByTitle("Project2");
        verify(projectRepository, times(1)).saveAll(projects);
    }

    private InputStream createTestExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Projects");

        // Header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Degree");
        header.createCell(1).setCellValue("Branch");
        header.createCell(2).setCellValue("Type");
        header.createCell(3).setCellValue("Domain");
        header.createCell(4).setCellValue("Title");
        header.createCell(5).setCellValue("Description");

        // Row 1: New project
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("B.Tech");
        row1.createCell(1).setCellValue("CSE");
        row1.createCell(2).setCellValue("Major");
        row1.createCell(3).setCellValue("AI");
        row1.createCell(4).setCellValue("New Project Title");
        row1.createCell(5).setCellValue("Description here");

        // Row 2: Duplicate project
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("M.Tech");
        row2.createCell(1).setCellValue("IT");
        row2.createCell(2).setCellValue("Minor");
        row2.createCell(3).setCellValue("ML");
        row2.createCell(4).setCellValue("Existing Project");
        row2.createCell(5).setCellValue("Already exists");

        // Convert to InputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Test
    void testSaveFileData() throws Exception {
        // Arrange
        InputStream excelFile = createTestExcel();
        when(projectRepository.findAllTitles()).thenReturn(Collections.singletonList("Existing Project"));

        // Act
        Map<String, Object> response = projectService.saveFileData(excelFile);

        // Assert
        assertTrue(response.get("savedProjects").toString().contains("row 1"));
        assertTrue(response.get("duplicates").toString().contains("row 2"));

        // Verify repository interaction
        verify(projectRepository, times(1)).saveAll(anyList());
    }

}
