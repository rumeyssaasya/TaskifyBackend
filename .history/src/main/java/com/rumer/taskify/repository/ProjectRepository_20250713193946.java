package com.rumer.taskify.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rumer.taskify.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectName(String projectName);
    // Project entity için CRUD işlemlerini sağlar.
    // Spring Data JPA otomatik olarak bu arayüzü implement eder.
    // Örneğin, save(), findById(), findAll(), delete() gibi metodlar kullanılabilir.

    
}
