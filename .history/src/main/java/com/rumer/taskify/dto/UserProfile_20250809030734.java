package com.rumer.taskify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfile {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    private String profileImageUrl;
}
