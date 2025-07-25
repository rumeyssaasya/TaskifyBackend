package com.rumer.taskify.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<Project> getAllProjects(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return projectRepository.findByUserId(user);
    }
    
    @GetMapping("/search")
    public List<Project> searchProjects(@RequestParam String projectName, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Project> projects = projectRepository.findByProjectName(projectName);
        return projects.stream()
                       .filter(project -> project.getUser().getId().equals(user.getId()))
                       .toList();
    }

    @PostMapping("create")
    public ResponseEntity<?> createProject(@RequestBody Project project, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        project.setUser(user);
        projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("path/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }
    
}
