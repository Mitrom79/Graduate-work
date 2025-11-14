package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CurrentUserService currentUserService;
    private final CommentMapper commentMapper;


    public Comments getCommentsByAdId(int adId) {
        log.info("Получение комментариев для объявления с ID: {}", adId);

        List<Comment> comments = commentRepository.findByAdPk(adId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());

        Comments result = new Comments();
        result.setCount(commentDTOs.size());
        result.setResults(commentDTOs);

        log.info("Найдено {} комментариев для объявления {}", commentDTOs.size(), adId);
        return result;
    }

    public CommentDTO addComment(int adId, CreateOrUpdateComment createOrUpdateComment) {
        log.info("Добавление комментария к объявлению с ID: {}", adId);

        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        Comment comment = new Comment();
        comment.setAuthor(currentUser.getId());
        comment.setAuthorFirstName(currentUser.getFirstName());
        comment.setAuthorImage(currentUser.getImage() != null ? currentUser.getImage() : "");
        comment.setCreatedAt(new Date());
        comment.setText(createOrUpdateComment.getText());
        comment.setAd(ad);



        int nextCommentId = getNextCommentId();
        comment.setPk(nextCommentId);

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий успешно добавлен к объявлению {}", adId);

        return commentMapper.commentToCommentDto(savedComment);
    }

    public void deleteComment(int adId, int commentId) {
        log.info("Удаление комментария {} из объявления {}", commentId, adId);

        User currentUser = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findByAdIdAndCommentId(adId, commentId);

        if (comment == null) {
            throw new RuntimeException("Комментарий не найден");
        }


        if (comment.getAuthor() != currentUser.getId() && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Недостаточно прав для удаления комментария");
        }

        commentRepository.delete(comment);
        log.info("Комментарий {} успешно удален", commentId);
    }

    public CommentDTO updateComment(int adId, int commentId, CreateOrUpdateComment updateComment) {
        log.info("Обновление комментария {} в объявлении {}", commentId, adId);

        User currentUser = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findByAdIdAndCommentId(adId, commentId);

        if (comment == null) {
            throw new RuntimeException("Комментарий не найден");
        }


        if (comment.getAuthor() != currentUser.getId()) {
            throw new RuntimeException("Недостаточно прав для редактирования комментария");
        }

        comment.setText(updateComment.getText());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Комментарий {} успешно обновлен", commentId);

        return commentMapper.commentToCommentDto(updatedComment);
    }

    private int getNextCommentId() {
        return commentRepository.findAll().stream()
                .mapToInt(Comment::getPk)
                .max()
                .orElse(0) + 1;
    }
}