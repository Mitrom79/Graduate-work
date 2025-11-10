package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateUser {
    @Schema(minLength = 2, maxLength = 16, description = "имя пользователя")
    private String firstName;
    @Schema(minLength = 2, maxLength = 16, description = "фамилия пользователя")
    private String lastName;
    @Schema(description = "телефон", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
}
