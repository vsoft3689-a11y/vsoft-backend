package com.vsoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vsoft.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	boolean existsByTitle(String title);
	List<Project> findByDegreeAndBranchAndTypeAndDomain(String degree, String branch, String type, String domain);
}
