package com.rumer.taskify.repository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.User;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectName(String projectName);
    @JsonBackReference // Bu anotasyon, JSON serileştirmede döngüsel referansları önler
    List<Project> findByUser(User user);
    // Project entity için CRUD işlemlerini sağlar.
    // Spring Data JPA otomatik olarak bu arayüzü implement eder.
    // Örneğin, save(), findById(), findAll(), delete() gibi metodlar kullanılabilir.

    
}
