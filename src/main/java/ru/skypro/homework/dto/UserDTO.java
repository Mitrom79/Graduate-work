package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class UserDTO {
    @Schema(description = "id пользователя")
    private int id;
    @Schema(description = "email пользователя")
    private String email;
    @Schema(description = "имя пользователя")
    private String firstName;
    @Schema(description = "фамилия пользователя")
    private String lastName;
    @Schema(description = "телефон", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
    @Schema(description = "роль пользователя")
    private Role role;
    @Schema(description = "ссылка на аватар пользователя")
    private String image;

    public UserDTO() {}
    public UserDTO(int id, String email, String firstName, String lastName, String phone, Role role, String image) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.image = image;
    }
}
