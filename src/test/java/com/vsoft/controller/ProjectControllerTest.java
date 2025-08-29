package com.vsoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsoft.entity.Project;
import com.vsoft.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetFilteredProjects_Success() throws Exception {
        Project project1 = new Project();

        project1.setId(1L);
        project1.setTitle("AI Project");
        project1.setDegree("B.Tech");
        project1.setType("Major");
        project1.setBranch("CSE");
        project1.setDomain("AI");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setTitle("Web App");
        project2.setDegree("B.Tech");
        project2.setType("Mini");
        project2.setBranch("CSE");
        project2.setDomain("Web");

        // Set properties for project1 and project2
        List<Project> projects = Arrays.asList(project1, project2);

        //mock service
        when(projectService.getFilteredProjects("B.Tech", "CSE", "Major", "AI")).thenReturn(Arrays.asList(project1));

        //perform request
        mockMvc.perform(get("/api/projects/{degree}/{branch}/{type}/{domain}",
                        "B.Tech", "CSE", "Major", "AI")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].title").value("AI Project"))
                        .andExpect(jsonPath("$[0].degree").value("B.Tech"))
                        .andExpect(jsonPath("$[0].type").value("Major"))
                        .andExpect(jsonPath("$[0].branch").value("CSE"))
                        .andExpect(jsonPath("$[0].domain").value("AI"));
    }

    @Test
    void testGetFilteredProjects_InvalidInput() throws Exception {
        // Perform request with invalid type
        mockMvc.perform(get("/api/projects/{degree}/{branch}/{type}/{domain}",
                        "B.Tech", "CSE", "InvalidType", "AI")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}
