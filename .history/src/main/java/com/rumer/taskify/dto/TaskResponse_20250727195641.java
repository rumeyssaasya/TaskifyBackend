package com.rumer.taskify.dto;

import lombok.Data;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private ProjectSummary project;

    @Data
    public static class ProjectSummary {
        private Long id;
        private String name;

        public ProjectSummary(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
