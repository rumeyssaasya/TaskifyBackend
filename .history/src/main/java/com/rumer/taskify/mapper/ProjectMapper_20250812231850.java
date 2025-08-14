package com.rumer.taskify.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.rumer.taskify.dto.ShareProjectUsers;
import com.rumer.taskify.dto.UserProfile;
import com.rumer.taskify.model.Project;

public class ProjectMapper {
        public ShareProjectUsers toShareProjectUsers(Project project) {
    ShareProjectUsers dto = new ShareProjectUsers();
    dto.setId(project.getId());
    dto.setProjectName(project.getProjectName());

    Set<UserProfile> collaborators = project.getCollaborators().stream()
        .map(user -> {
            UserProfile profile = new UserProfile();
            profile.setFullName(user.getFullName());
            profile.setEmail(user.getEmail());
            profile.setGender(user.getGender());
            profile.setProfileImageUrl(user.getProfileImageUrl());
            return profile;
        })
        .collect(Collectors.toSet());

    dto.setCollaborators(collaborators);
    return dto;
}
}
