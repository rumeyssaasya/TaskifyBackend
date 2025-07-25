package com.rumer.taskify.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // id otomatik artan
    private Long id;
    private String projectName; // Proje adı

    @OneToMany(mappedBy = "project") // Task sınıfında project ile ilişkilendirildi
    List<Task> tasks; // Projeye ait görevler
}
