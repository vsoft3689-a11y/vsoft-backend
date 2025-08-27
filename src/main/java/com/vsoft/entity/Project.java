package com.vsoft.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Degree is required")
    private String degree; // BTECH, MTECH, MBA, MCA
    @NotBlank(message = "Branch is required")
    private String branch; // CSE, EEE, ECE, MECH
    @Pattern(regexp = "Major|Mini", message = "Type must be either 'major' or 'mini'")
    private String type; // Major Project or Minor Project
    @NotBlank(message = "Domain is required")
    private String domain; // FullStack, Cyber security
    @NotBlank(message = "Title is required")
    private String title; // Project name
    @NotBlank(message = "Description is required")
    private String description;
}
