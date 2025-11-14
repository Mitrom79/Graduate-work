package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    @Test
    void getAdImage_ShouldReturnImage() throws IOException {
        String imageId = "test-image-id";
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};
        when(imageService.getAdImage(imageId)).thenReturn(imageData);

        ResponseEntity<byte[]> response = imageController.getAdImage(imageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageData, response.getBody());
    }

    @Test
    void getAdImage_WhenNotFound_ShouldReturnNotFound() throws IOException {
        String imageId = "non-existent-image";
        when(imageService.getAdImage(imageId)).thenThrow(new IOException("Image not found"));

        ResponseEntity<byte[]> response = imageController.getAdImage(imageId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserImage_ShouldReturnImage() throws IOException {
        String imageId = "user-avatar-id";
        byte[] imageData = new byte[]{5, 4, 3, 2, 1};
        when(imageService.getUserImage(imageId)).thenReturn(imageData);

        ResponseEntity<byte[]> response = imageController.getUserImage(imageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageData, response.getBody());
    }

    @Test
    void getUserImage_WhenNotFound_ShouldReturnNotFound() throws IOException {
        String imageId = "non-existent-user-image";
        when(imageService.getUserImage(imageId)).thenThrow(new IOException("User image not found"));

        ResponseEntity<byte[]> response = imageController.getUserImage(imageId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
