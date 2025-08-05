package com.vsoft.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="project")
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String degree; // BTECH, MTECH, MBA, MCA
	private String branch; // CSE, EEE, ECE, MECH
	private String type; // Major Project or Minor Project
	private String domain; // FullStack, Cyber security
	private String title;  // Project name
	

}
