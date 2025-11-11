package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.CurrentUserService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserController userController;

    @Test
    void setNewPassword_ShouldReturnOk() {
        // Arrange
        NewPassword newPassword = new NewPassword();
        newPassword.setNewPassword("newPassword123");

        User mockUser = new User();
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        // Act
        ResponseEntity<Void> response = userController.setNewPassword(newPassword);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updatePassword(mockUser, "newPassword123");
    }

    @Test
    void getMe_ShouldReturnUserDTO() {
        // Arrange
        UserDTO expectedUserDTO = new UserDTO(1, "test@mail.ru", "John", "Doe", "+79991234567", null, null);
        when(userService.getCurrentUserDTO()).thenReturn(expectedUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getMe();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
    }

    @Test
    void updateMe_ShouldReturnUpdatedUser() {
        // Arrange
        UpdateUser updateUserRequest = new UpdateUser();
        updateUserRequest.setFirstName("Jane");
        updateUserRequest.setLastName("Smith");
        updateUserRequest.setPhone("+79997654321");

        UpdateUser expectedResponse = new UpdateUser();
        expectedResponse.setFirstName("Jane");
        expectedResponse.setLastName("Smith");
        expectedResponse.setPhone("+79997654321");

        User mockUser = new User();
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(userService.updateUser(mockUser, updateUserRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UpdateUser> response = userController.updateMe(updateUserRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateImage_ShouldReturnOk() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        // Act
        ResponseEntity<Void> response = userController.updateImage(mockFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateUserImage(mockFile);
    }

    @Test
    void getMyImage_WithImage_ShouldReturnImage() {
        // Arrange
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};
        when(userService.getUserImage()).thenReturn(imageData);

        // Act
        ResponseEntity<byte[]> response = userController.getMyImage();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageData, response.getBody());
        assertEquals("image/jpeg", response.getHeaders().getContentType().toString());
    }

    @Test
    void getMyImage_WithoutImage_ShouldReturnNotFound() {
        // Arrange
        when(userService.getUserImage()).thenReturn(new byte[0]);

        // Act
        ResponseEntity<byte[]> response = userController.getMyImage();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserImage_WithValidId_ShouldReturnImage() {
        // Arrange
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};
        when(userService.getUserImage(1)).thenReturn(imageData);

        // Act
        ResponseEntity<byte[]> response = userController.getUserImage(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageData, response.getBody());
        assertEquals("image/jpeg", response.getHeaders().getContentType().toString());
    }

    @Test
    void getUserImage_WithInvalidId_ShouldReturnNotFound() {
        // Arrange
        when(userService.getUserImage(999)).thenReturn(new byte[0]);

        // Act
        ResponseEntity<byte[]> response = userController.getUserImage(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}