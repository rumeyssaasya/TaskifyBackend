package com.rumer.taskify.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rumer.taskify.dto.ShareProjectUsers;
import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.Task;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.ProjectRepository;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.service.ProjectService;
import com.rumer.taskify.service.TaskService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.ArrayList;
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
    private final TaskService taskService;

    @GetMapping
    public List<Project> getAllProjects(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        List<Project> ownedProjects = projectRepository.findByUser(user);
        List<Project> sharedProjects = new ArrayList<>(user.getSharedProjects()); // paylaşılan projeler
        
        ownedProjects.addAll(sharedProjects);
        return ownedProjects;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Project project = projectRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        boolean isOwner = project.getUser().getId().equals(user.getId());
        boolean isCollaborator = project.getCollaborators() != null && project.getCollaborators().contains(user);

        if (!isOwner && !isCollaborator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(project);
    }


    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.findTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/{id}/shared")
    public ResponseEntity<ShareProjectUsers> getSharedProject(@PathVariable Long id) {
        ShareProjectUsers dto = projectService.getProjectWithCollaborators(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        project.setUser(user);
        projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }


    @PostMapping("/{projectId}/share")
    public ResponseEntity<?> shareProject(@PathVariable Long projectId,
                                        @RequestParam String userName,
                                        Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        try {
            Project updatedProject = projectService.shareProject(projectId, userName, currentUser);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

   @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Project updatedProject, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        boolean isOwner = project.getUser().getId().equals(user.getId());
        boolean isCollaborator = project.getCollaborators() != null && project.getCollaborators().contains(user);

        if (!isOwner && !isCollaborator) {
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
        User user = userRepository.findByUsername(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Project project = projectRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));

        boolean isOwner = project.getUser().getId().equals(user.getId());

        boolean isCollaborator = false;
        if (project.getCollaborators() != null) {
            isCollaborator = project.getCollaborators()
                                .stream()
                                .anyMatch(collaborator -> collaborator.getId().equals(user.getId()));
        }

        if (!isOwner && !isCollaborator) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Proje silme yetkiniz yok.");
        }

        projectRepository.delete(project);
        return ResponseEntity.ok("Proje başarıyla silindi.");
    }
}
