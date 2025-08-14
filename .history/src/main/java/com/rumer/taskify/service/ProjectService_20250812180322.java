package com.rumer.taskify.service;

import org.springframework.stereotype.Service;

import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.ProjectRepository;
import com.rumer.taskify.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Owner ve collaborator projelerini getir
    public List<Project> getProjectsByUser(User user) {
        List<Project> ownedProjects = projectRepository.findByUser(user);
        List<Project> sharedProjects = new ArrayList<>(user.getSharedProjects());
        ownedProjects.addAll(sharedProjects);
        return ownedProjects;
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project updatedProject, User currentUser) {
        Project existingProject = getProjectById(id);
        // Sadece owner düzenleyebilir
        if (!existingProject.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Yetkiniz yok.");
        }
        existingProject.setProjectName(updatedProject.getProjectName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setStatus(updatedProject.getStatus());
        existingProject.setStartDate(updatedProject.getStartDate());
        existingProject.setEndDate(updatedProject.getEndDate());
        return projectRepository.save(existingProject);
    }

    public void deleteProject(Long id, User currentUser) {
        Project project = getProjectById(id);
        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Yetkiniz yok.");
        }
        projectRepository.delete(project);
    }

    // Proje paylaşımı için kullanıcı ekleme
    public Project shareProject(Long projectId, Long userIdToShare, User currentUser) {
        Project project = getProjectById(projectId);

        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Sadece proje sahibi paylaşım yapabilir.");
        }

        User userToShare = userRepository.findById(userIdToShare)
            .orElseThrow(() -> new RuntimeException("Paylaşılacak kullanıcı bulunamadı"));

        if (project.getCollaborators() == null) {
            project.setCollaborators(new HashSet<>());
        }
        project.getCollaborators().add(userToShare);

        if (userToShare.getSharedProjects() == null) {
            userToShare.setSharedProjects(new HashSet<>());
        }
        userToShare.getSharedProjects().add(project);

        // Kaydetme işlemi sadece projectRepository.save ile genelde yeterli,
        // ancak gerektiğinde userRepository.save(userToShare) da yapılabilir.
        return projectRepository.save(project);
    }


}
