package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class CommentDTO {
    @Schema(description = "id автора комментария")
    private int author;
    @Schema(description = "ссылка на аватар автора комментария")
    private String authorImage;
    @Schema(description = "имя создателя комментария")
    private String authorFirstName;
    @Schema(description = "дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970")
    private Date createdAt;
    @Schema(description = "id комментария")
    private int pk;
    @Schema(description = "текст комментария")
    private String text;

    public CommentDTO() {}

    public CommentDTO(int author, String authorImage, String authorFirstName, Date createdAt, int pk, String text) {
        this.author = author;
        this.authorImage = authorImage;
        this.authorFirstName = authorFirstName;
        this.createdAt = createdAt;
        this.pk = pk;
        this.text = text;
    }
}
