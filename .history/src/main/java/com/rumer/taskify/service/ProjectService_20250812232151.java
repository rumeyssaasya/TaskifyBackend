package com.rumer.taskify.service;

import org.springframework.stereotype.Service;

import com.rumer.taskify.dto.ShareProjectUsers;
import com.rumer.taskify.mapper.ProjectMapper;
import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.User;
import com.rumer.taskify.repository.ProjectRepository;
import com.rumer.taskify.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    


    public ShareProjectUsers getProjectWithCollaborators(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Proje bulunamadı"));
        return ProjectMapper.toShareProjectUsers(project);
    }

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
    /**
     * @param projectId
     * @param userName
     * @param currentUser
     * @return
     */
    @Transactional
    public Project shareProject(Long projectId, String userName, User currentUser) {
        Project project = getProjectById(projectId);


        User userToShare = userRepository.findByUsername(userName)
            .orElseThrow(() -> new EntityNotFoundException("Paylaşılacak kullanıcı bulunamadı"));

        if (project.getCollaborators() == null) {
            project.setCollaborators(new HashSet<>());
        }
        if (userToShare.getSharedProjects() == null) {
            userToShare.setSharedProjects(new HashSet<>());
        }

        if (project.getCollaborators().contains(userToShare)) {
            throw new IllegalStateException("Kullanıcı zaten projeye ekli.");
        }

        project.getCollaborators().add(userToShare);
        userToShare.getSharedProjects().add(project);

        return projectRepository.save(project);
    }



}
