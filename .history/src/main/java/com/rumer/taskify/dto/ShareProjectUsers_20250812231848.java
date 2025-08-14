package com.rumer.taskify.dto;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.rumer.taskify.model.Project;


public class ShareProjectUsers {
    private Long id;
    private String projectName;
    private Set<UserProfile> collaborators = new HashSet<>();


    
  
}

