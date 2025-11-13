package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    CommentDTO commentToCommentDto(Comment comment);

    default Long mapCreatedAt(java.util.Date date) {
        return date == null ? null : date.getTime();
    }
}