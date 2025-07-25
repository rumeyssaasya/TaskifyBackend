package com.rumer.taskify.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.ProjectRepository;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.service.ProjectService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<Project> getAllProjects(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        return projectRepository.findByUser(user);
    }
    
    @GetMapping("/search")
    public List<Project> searchProjects(@RequestParam String projectName, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Project> projects = projectRepository.findByProjectName(projectName);
        return projects.stream()
                       .filter(project -> project.getUser().getId().equals(user.getId()))
                       .toList();
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        project.setUser(user);
        projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Project updatedProject, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        if (!project.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Proje Güncellenemez.");
        }

        project.setProjectName(updatedProject.getProjectName());
        project.setDescription(updatedProject.getDescription());
        project.setStatus(updatedProject.getStatus());
        project.setStartDate(updatedProject.getStartDate());
        project.setEndDate(updatedProject.getEndDate());

        projectRepository.save(project);
        return ResponseEntity.ok(project);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        if (!project.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Proje Silinemedi.");
        }
        projectRepository.delete(project);
        return ResponseEntity.ok("Proje başarıyla silindi.");
    }
}
