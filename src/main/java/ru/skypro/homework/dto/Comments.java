package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class Comments {
    @Schema(description = "Количество комментариев", example = "1")
    private Integer count;

    @Schema(description = "Список комментариев")
    private List<CommentDTO> results;
}