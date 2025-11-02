package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Register {
    @Schema(minLength = 4, maxLength = 32, description = "логин")
    private String username;
    @Schema(minLength = 8, maxLength = 16, description = "пароль")
    private String password;
    @Schema(minLength = 2, maxLength = 16, description = "имя")
    private String firstName;
    @Schema(minLength = 2, maxLength = 16, description = "фамилия")
    private String lastName;
    @Schema(description = "телефон", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
    @Schema(description = "роль")
    private Role role;
}
