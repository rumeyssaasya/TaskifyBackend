package com.rumer.taskify.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.rumer.taskify.dto.ShareProjectUsers;
import com.rumer.taskify.dto.UserProfile;
import com.rumer.taskify.model.Project;

public class ProjectMapper {
         public static ShareProjectUsers toShareProjectUsers(Project project) {
        ShareProjectUsers dto = new ShareProjectUsers();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());

        // Null kontrolü
        if (project.getCollaborators() != null) {
            dto.setCollaborators(project.getCollaborators().stream()
                .map(user -> {
                    UserProfile profile = new UserProfile();
                    profile.setFullName(user.getFullName());
                    profile.setEmail(user.getEmail());
                    profile.setGender(user.getGender());
                    profile.setProfileImageUrl(user.getProfileImageUrl());
                    return profile;
                })
                .collect(Collectors.toSet()));
        } else {
            dto.setCollaborators(new HashSet<>());
        }

        return dto;
    }
}
