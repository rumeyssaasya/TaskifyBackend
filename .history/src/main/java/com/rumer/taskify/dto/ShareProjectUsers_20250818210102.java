package com.rumer.taskify.dto;
import java.util.HashSet;
import java.util.Set;
public class ShareProjectUsers {
    private Long id;
    private String projectName;
    private Set<UserProfile> collaborators = new HashSet<>();
     
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Set<UserProfile> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<UserProfile> collaborators) {
        this.collaborators = collaborators;
    }
    
  
}

