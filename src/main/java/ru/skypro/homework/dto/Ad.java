package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class Ad {
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
}
