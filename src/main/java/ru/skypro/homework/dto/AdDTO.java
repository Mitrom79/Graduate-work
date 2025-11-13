package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdDTO {
    @Schema(description = "ID автора объявления", example = "123")
    private int author;
    @Schema(description = "ссылка на картинку объявления", example = "https://example.com/image.jpg")
    private String image;
    @Schema(description = "id объявления", example = "123")
    private int pk;
    @Schema(description = "цена объявления", example = "123")
    private int price;
    @Schema(description = "заголовок объявления", example = "Объявление 1")
    private String title;
    @Schema(description = "описание объявления", example = "Описание объявления")
    private String description;

    public AdDTO(int author, String image, int pk, int price, String title, String description) {
        this.author = author;
        this.image = image;
        this.pk = pk;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public AdDTO() {}
}