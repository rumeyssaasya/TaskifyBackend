package com.rumer.taskify.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Project {
    private Long id;
    private String projectName; // Proje adı

    @OneToMany(mappedBy = "project") // Task sınıfında project ile ilişkilendirildi
    List<Task> tasks; // Projeye ait görevler
}
