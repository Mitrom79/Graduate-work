package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Login {
    @Schema(minLength = 8, maxLength = 16, description = "логин")
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;

    @Schema(minLength = 8, maxLength = 16, description = "пароль")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    private String password;
}