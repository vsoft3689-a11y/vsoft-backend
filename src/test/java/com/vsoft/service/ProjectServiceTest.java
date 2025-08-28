package com.vsoft.service;

import com.vsoft.entity.Project;
import com.vsoft.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

}
