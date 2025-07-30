package com.rumer.taskify.dto;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private String status;
    @Nullable
    private Long projectId; // null veya 0 olursa proje ilişkilendirilmez
}
