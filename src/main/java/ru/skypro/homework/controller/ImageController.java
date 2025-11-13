package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.OPTIONS}
)
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/images/ads/{id}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getAdImage(@PathVariable String id) {
        try {
            byte[] image = imageService.getAdImage(id);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            log.error("Ошибка при получении изображения объявления: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/images/users/{id}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getUserImage(@PathVariable String id) {
        try {
            byte[] image = imageService.getUserImage(id);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            log.error("Ошибка при получении изображения пользователя: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
