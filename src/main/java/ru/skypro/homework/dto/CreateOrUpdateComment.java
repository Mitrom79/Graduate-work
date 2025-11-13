package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateOrUpdateComment {
    @Schema(description = "Текст комментария", example = "Отличное объявление!")
    private String text;
}
