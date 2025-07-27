package com.rumer.taskify.dto;

import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private string status;
    private Long projectId; // null veya 0 olursa proje ilişkilendirilmez
}
