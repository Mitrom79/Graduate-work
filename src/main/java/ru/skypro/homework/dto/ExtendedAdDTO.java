package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExtendedAdDTO {
    @Schema(description = "id объявления")
    private int pk;
    @Schema(description = "имя автора объявления")
    private String authorFirstName;
    @Schema(description = "фамилия автора объявления")
    private String authorLastName;
    @Schema(description = "описание объявления")
    private String description;
    @Schema(description = "email автора объявления")
    private String email;
    @Schema(description = "ссылка на картинку объявления")
    private String image;
    @Schema(description = "телефон автора объявления")
    private String phone;
    @Schema(description = "цена объявления")
    private int price;
    @Schema(description = "заголовок объявления")
    private String title;

    public ExtendedAdDTO() {}

    public ExtendedAdDTO(String title, int price, String phone, String image, String email, String description, String authorLastName, String authorFirstName, int pk) {
        this.title = title;
        this.price = price;
        this.phone = phone;
        this.image = image;
        this.email = email;
        this.description = description;
        this.authorLastName = authorLastName;
        this.authorFirstName = authorFirstName;
        this.pk = pk;
    }
}
