package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@DisplayName("Тестирование CommentController")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private final String USERNAME = "test@example.com";

    private CommentDTO createCommentDTO(int pk, int author, String authorFirstName, String text) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPk(pk);
        commentDTO.setAuthor(author);
        commentDTO.setAuthorFirstName(authorFirstName);
        commentDTO.setText(text);
        return commentDTO;
    }

    @Nested
    @DisplayName("Тесты получения комментариев")
    class GetCommentsTests {

        @Test
        @WithMockUser
        @DisplayName("Получение комментариев по ID объявления - должен вернуть OK")
        void getCommentsByAdId_ShouldReturnOk() throws Exception {
            Comments comments = new Comments();
            comments.setCount(2);
            comments.setResults(List.of(
                    createCommentDTO(1, 1, "John", "Great ad!"),
                    createCommentDTO(2, 2, "Jane", "Nice product!")
            ));

            when(commentService.getCommentsByAdId(1)).thenReturn(comments);

            mockMvc.perform(get("/ads/1/comments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(2))
                    .andExpect(jsonPath("$.results.length()").value(2));
        }

        @Test
        @WithMockUser
        @DisplayName("Получение комментариев для несуществующего объявления - должен вернуть OK с пустым списком")
        void getCommentsByAdId_WhenNoComments_ShouldReturnOk() throws Exception {
            Comments comments = new Comments();
            comments.setCount(0);
            comments.setResults(List.of());

            when(commentService.getCommentsByAdId(999)).thenReturn(comments);

            mockMvc.perform(get("/ads/999/comments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(0))
                    .andExpect(jsonPath("$.results.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Тесты добавления комментариев")
    class AddCommentTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Добавление комментария - должен вернуть OK")
        void addCommentByAdId_ShouldReturnOk() throws Exception {
            CreateOrUpdateComment createComment = new CreateOrUpdateComment();
            createComment.setText("This is a test comment");

            CommentDTO commentDTO = createCommentDTO(1, 1, "Test User", "This is a test comment");

            when(commentService.addComment(eq(1), any(CreateOrUpdateComment.class))).thenReturn(commentDTO);

            mockMvc.perform(post("/ads/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createComment)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pk").value(1))
                    .andExpect(jsonPath("$.text").value("This is a test comment"));
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Добавление комментария с пустым текстом - должен вернуть Bad Request")
        void addCommentByAdId_WithEmptyText_ShouldReturnBadRequest() throws Exception {
            CreateOrUpdateComment createComment = new CreateOrUpdateComment();
            createComment.setText("   ");

            mockMvc.perform(post("/ads/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createComment)))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).addComment(anyInt(), any());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Добавление комментария с null текстом - должен вернуть Bad Request")
        void addCommentByAdId_WithNullText_ShouldReturnBadRequest() throws Exception {
            CreateOrUpdateComment createComment = new CreateOrUpdateComment();
            createComment.setText(null);

            mockMvc.perform(post("/ads/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createComment)))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).addComment(anyInt(), any());
        }
    }

    @Nested
    @DisplayName("Тесты обновления комментариев")
    class UpdateCommentTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление комментария - должен вернуть OK")
        void updateCommentByAdId_ShouldReturnOk() throws Exception {
            CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
            updateComment.setText("Updated comment text");

            CommentDTO commentDTO = createCommentDTO(1, 1, "Test User", "Updated comment text");

            when(commentService.updateComment(eq(1), eq(1), any(CreateOrUpdateComment.class))).thenReturn(commentDTO);

            mockMvc.perform(patch("/ads/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateComment)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.text").value("Updated comment text"));
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление комментария с пустым текстом - должен вернуть Bad Request")
        void updateCommentByAdId_WithEmptyText_ShouldReturnBadRequest() throws Exception {
            CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
            updateComment.setText("   ");

            mockMvc.perform(patch("/ads/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateComment)))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).updateComment(anyInt(), anyInt(), any());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление комментария с null текстом - должен вернуть Bad Request")
        void updateCommentByAdId_WithNullText_ShouldReturnBadRequest() throws Exception {
            CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
            updateComment.setText(null);

            mockMvc.perform(patch("/ads/1/comments/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateComment)))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).updateComment(anyInt(), anyInt(), any());
        }
    }

    @Nested
    @DisplayName("Тесты удаления комментариев")
    class DeleteCommentTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Удаление комментария - должен вернуть OK")
        void deleteCommentByAdId_ShouldReturnOk() throws Exception {
            mockMvc.perform(delete("/ads/1/comments/1")
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(commentService).deleteComment(1, 1);
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Удаление комментария с разными ID - должен вызвать сервис с правильными параметрами")
        void deleteCommentByAdId_WithDifferentIds_ShouldCallServiceWithCorrectParams() throws Exception {
            mockMvc.perform(delete("/ads/123/comments/456")
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(commentService).deleteComment(123, 456);
        }
    }

    @Nested
    @DisplayName("Тесты безопасности")
    class SecurityTests {

        @Test
        @DisplayName("Получение комментариев без аутентификации - должен вернуть Unauthorized")
        void getCommentsWithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/ads/1/comments"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("Доступ с аутентификацией - должен вернуть OK")
        void accessWithAuth_ShouldReturnOk() throws Exception {
            Comments comments = new Comments();
            comments.setCount(0);
            comments.setResults(List.of());
            when(commentService.getCommentsByAdId(1)).thenReturn(comments);

            mockMvc.perform(get("/ads/1/comments"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Добавление комментария без аутентификации - должен вернуть Forbidden (из-за CSRF)")
        void addCommentWithoutAuth_ShouldReturnForbidden() throws Exception {
            CreateOrUpdateComment createComment = new CreateOrUpdateComment();
            createComment.setText("Test comment");

            mockMvc.perform(post("/ads/1/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createComment)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Обновление комментария без аутентификации - должен вернуть Forbidden (из-за CSRF)")
        void updateCommentWithoutAuth_ShouldReturnForbidden() throws Exception {
            CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
            updateComment.setText("Updated comment");

            mockMvc.perform(patch("/ads/1/comments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateComment)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Удаление комментария без аутентификации - должен вернуть Forbidden (из-за CSRF)")
        void deleteCommentWithoutAuth_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(delete("/ads/1/comments/1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Тесты обработки исключений")
    class ExceptionHandlingTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Добавление комментария к несуществующему объявлению - должен вернуть Internal Server Error")
        void addCommentToNonExistentAd_ShouldPropagateServiceError() throws Exception {
            CreateOrUpdateComment createComment = new CreateOrUpdateComment();
            createComment.setText("Test comment");

            when(commentService.addComment(eq(999), any(CreateOrUpdateComment.class)))
                    .thenThrow(new RuntimeException("Объявление не найдено"));

            mockMvc.perform(post("/ads/999/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createComment)))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Удаление несуществующего комментария - должен вернуть Internal Server Error")
        void deleteNonExistentComment_ShouldPropagateServiceError() throws Exception {
            doThrow(new RuntimeException("Комментарий не найден"))
                    .when(commentService).deleteComment(1, 999);

            mockMvc.perform(delete("/ads/1/comments/999")
                            .with(csrf()))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление несуществующего комментария - должен вернуть Internal Server Error")
        void updateNonExistentComment_ShouldPropagateServiceError() throws Exception {
            CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
            updateComment.setText("Updated text");

            when(commentService.updateComment(eq(1), eq(999), any(CreateOrUpdateComment.class)))
                    .thenThrow(new RuntimeException("Комментарий не найден"));

            mockMvc.perform(patch("/ads/1/comments/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateComment)))
                    .andExpect(status().isInternalServerError());
        }
    }
}