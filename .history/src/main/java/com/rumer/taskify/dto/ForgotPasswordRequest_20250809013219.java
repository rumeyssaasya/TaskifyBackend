// ForgotPasswordRequest.java
package com.rumer.taskify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    private String username;

    @Email(message = "Lütfen geçerli bir email adresi girin")
    private String email;

    // getter-setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}