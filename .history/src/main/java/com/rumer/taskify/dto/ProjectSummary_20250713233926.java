package com.rumer.taskify.dto;

import lombok.Data;

@Data
public static class TaskResponse {
    private Long id;
    private String name;

    public ProjectSummary(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

