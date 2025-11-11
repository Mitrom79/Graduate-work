package ru.skypro.homework.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void getMe_WithAuth_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk());
    }

    @Test
    void getMe_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void updateImage_WithValidImage_ShouldReturnOk() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/users/me/image")
                        .file(image))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void setNewPassword_ShouldReturnOk() throws Exception {
        String newPasswordJson = """
            {
                "newPassword": "newPassword123"
            }
            """;

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPasswordJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void updateUser_ShouldReturnOk() throws Exception {
        String updateUserJson = """
            {
                "firstName": "Jane",
                "lastName": "Smith", 
                "phone": "+79991234567"
            }
            """;

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void getMyImage_WhenNoImage_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/users/me/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = {"USER"})
    void getUserImage_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/users/999/image"))
                .andExpect(status().isNotFound());
    }
}