package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentDTO {
    @Schema(description = "ID автора комментария", example = "123")
    private int author;

    @Schema(description = "Ссылка на аватар автора комментария", example = "/images/users/avatar.jpg")
    private String authorImage;

    @Schema(description = "Имя автора комментария", example = "Иван")
    private String authorFirstName;

    @Schema(description = "Дата создания комментария в миллисекундах", example = "1639492800000")
    private Long createdAt;

    @Schema(description = "ID комментария", example = "1")
    private int pk;

    @Schema(description = "Текст комментария", example = "Отличное объявление!")
    private String text;
}