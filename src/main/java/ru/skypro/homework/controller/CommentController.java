package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
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
    public Comments getCommentsByAdId(@PathVariable int id) {
        return new Comments();
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment by ad id", description = "Добавление комментария к объявлению",
            parameters = @Parameter(name = "id", description = "id of ad"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public CreateOrUpdateComment addCommentByAdId(@PathVariable int id) {
        return new CreateOrUpdateComment();
    }

    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    @Operation(summary= "Delete comment by ad id", description = "Удаление комментария",
            parameters = {
                    @Parameter(name = "adId", description = "id of ad"),
                    @Parameter(name = "commentId", description = "id of comment")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public void deleteCommentByAdId(@PathVariable int adId, @PathVariable int commentId) {
    }

    @PatchMapping("/ads/{adId}/comments/{commentId}")
    @Operation(summary = "Update comment by ad id", description = "Обновление комментария",
            parameters = {
                    @Parameter(name = "adId", description = "id of ad"),
                    @Parameter(name = "commentId", description = "id of comment")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    public CreateOrUpdateComment updateCommentByAdId(@PathVariable int adId, @PathVariable int commentId) {
        return new CreateOrUpdateComment();
    }
}