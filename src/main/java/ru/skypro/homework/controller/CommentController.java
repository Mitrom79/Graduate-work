package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS }
)
@Schema(description = "Контроллер для работы с комментариями")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get comments by ad id", description = "Получение комментариев объявления",
            parameters = @Parameter(name = "id", description = "id of ad"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public ResponseEntity<Comments> getCommentsByAdId(@PathVariable int id) {
        Comments comments = commentService.getCommentsByAdId(id);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment by ad id", description = "Добавление комментария к объявлению",
            parameters = @Parameter(name = "id", description = "id of ad"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public ResponseEntity<CommentDTO> addCommentByAdId(
            @PathVariable int id,
            @RequestBody CreateOrUpdateComment createOrUpdateComment) {

        if (createOrUpdateComment.getText() == null || createOrUpdateComment.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CommentDTO commentDTO = commentService.addComment(id, createOrUpdateComment);
        return ResponseEntity.ok(commentDTO);
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(summary= "Delete comment by ad id", description = "Удаление комментария",
            parameters = {
                    @Parameter(name = "adId", description = "id of ad"),
                    @Parameter(name = "commentId", description = "id of comment")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public ResponseEntity<Void> deleteCommentByAdId(
            @PathVariable int adId,
            @PathVariable int commentId) {

        commentService.deleteComment(adId, commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(summary = "Update comment by ad id", description = "Обновление комментария",
            parameters = {
                    @Parameter(name = "adId", description = "id of ad"),
                    @Parameter(name = "commentId", description = "id of comment")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public ResponseEntity<CommentDTO> updateCommentByAdId(
            @PathVariable int adId,
            @PathVariable int commentId,
            @RequestBody CreateOrUpdateComment updateComment) {

        if (updateComment.getText() == null || updateComment.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CommentDTO commentDTO = commentService.updateComment(adId, commentId, updateComment);
        return ResponseEntity.ok(commentDTO);
    }
}