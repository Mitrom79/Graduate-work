package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.service.AdService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdController.class)
@DisplayName("Тестирование AdController")
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdService adService;

    private final String USERNAME = "test@example.com";

    @Nested
    @DisplayName("Тесты получения объявлений")
    class GetAdsTests {

        @Test
        @WithMockUser
        @DisplayName("Получение всех объявлений - должен вернуть OK")
        void getAllAds_ShouldReturnOk() throws Exception {
            when(adService.getAllAds()).thenReturn(new Ads());

            mockMvc.perform(get("/ads"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("Получение объявления по ID - должен вернуть OK")
        void getAd_ShouldReturnOk() throws Exception {
            when(adService.getAd(anyInt())).thenReturn(new AdDTO());

            mockMvc.perform(get("/ads/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Получение объявлений текущего пользователя - должен вернуть OK")
        void getMyAds_ShouldReturnOk() throws Exception {
            when(adService.getMyAds()).thenReturn(new Ads());

            mockMvc.perform(get("/ads/me"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Тесты создания объявлений")
    class CreateAdTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Создание нового объявления - должен вернуть Created")
        void addAd_ShouldReturnCreated() throws Exception {
            CreateOrUpdateAd createAd = new CreateOrUpdateAd();
            createAd.setTitle("Test Ad");
            createAd.setPrice(1000);
            createAd.setDescription("Test Description");

            MockMultipartFile properties = new MockMultipartFile(
                    "ad",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(createAd)
            );
            MockMultipartFile image = new MockMultipartFile(
                    "image",
                    "test.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            when(adService.addAd(any(CreateOrUpdateAd.class), any())).thenReturn(new AdDTO());

            mockMvc.perform(multipart("/ads")
                            .file(properties)
                            .file(image)
                            .with(csrf()))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Тесты обновления объявлений")
    class UpdateAdTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление объявления - должен вернуть OK")
        void updateAd_ShouldReturnOk() throws Exception {
            CreateOrUpdateAd updateAd = new CreateOrUpdateAd();
            updateAd.setTitle("Updated Ad");
            updateAd.setPrice(1500);
            updateAd.setDescription("Updated Description");

            when(adService.updateAd(anyInt(), any(CreateOrUpdateAd.class))).thenReturn(updateAd);

            mockMvc.perform(patch("/ads/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateAd)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Обновление изображения объявления - должен вернуть OK")
        void updateImage_ShouldReturnOk() throws Exception {
            MockMultipartFile image = new MockMultipartFile(
                    "image",
                    "new-image.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "new image content".getBytes()
            );

            mockMvc.perform(multipart("/ads/1/image")
                            .file(image)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            }))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Тесты удаления объявлений")
    class DeleteAdTests {

        @Test
        @WithMockUser(username = USERNAME)
        @DisplayName("Удаление объявления - должен вернуть No Content")
        void deleteAd_ShouldReturnNoContent() throws Exception {
            mockMvc.perform(delete("/ads/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Тесты безопасности")
    class SecurityTests {

        @Test
        @DisplayName("Доступ без аутентификации - должен вернуть Unauthorized")
        void accessWithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/ads"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("Доступ с аутентификацией - должен вернуть OK")
        void accessWithAuth_ShouldReturnOk() throws Exception {
            when(adService.getAllAds()).thenReturn(new Ads());

            mockMvc.perform(get("/ads"))
                    .andExpect(status().isOk());
        }
    }
}