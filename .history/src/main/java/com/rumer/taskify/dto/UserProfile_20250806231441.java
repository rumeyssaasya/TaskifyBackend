package com.rumer.taskify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfile {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String profileImageUrl;
}
