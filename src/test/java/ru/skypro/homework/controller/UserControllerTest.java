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
        NewPassword newPassword = new NewPassword();
        newPassword.setNewPassword("newPassword123");

        User mockUser = new User();
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        ResponseEntity<Void> response = userController.setNewPassword(newPassword);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updatePassword(mockUser, "newPassword123");
    }

    @Test
    void getMe_ShouldReturnUserDTO() {
        UserDTO expectedUserDTO = new UserDTO(1, "test@mail.ru", "John", "Doe", "+79991234567", null, null);
        when(userService.getCurrentUserDTO()).thenReturn(expectedUserDTO);

        ResponseEntity<UserDTO> response = userController.getMe();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
    }

    @Test
    void updateMe_ShouldReturnUpdatedUser() {
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

        ResponseEntity<UpdateUser> response = userController.updateMe(updateUserRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateImage_ShouldReturnOk() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        ResponseEntity<Void> response = userController.updateImage(mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateUserImage(mockFile);
    }

    @Test
    void updateImage_WithEmptyFile_ShouldReturnBadRequest() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        ResponseEntity<Void> response = userController.updateImage(mockFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).updateUserImage(any());
    }

    @Test
    void updateImage_WithNullFile_ShouldReturnBadRequest() {
        ResponseEntity<Void> response = userController.updateImage(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).updateUserImage(any());
    }

    @Test
    void setNewPassword_WithException_ShouldReturnInternalServerError() {
        NewPassword newPassword = new NewPassword();
        newPassword.setNewPassword("newPassword123");

        User mockUser = new User();
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        doThrow(new RuntimeException("DB error")).when(userService).updatePassword(mockUser, "newPassword123");

        ResponseEntity<Void> response = userController.setNewPassword(newPassword);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getMe_WithException_ShouldReturnInternalServerError() {
        when(userService.getCurrentUserDTO()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<UserDTO> response = userController.getMe();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateMe_WithException_ShouldReturnInternalServerError() {
        UpdateUser updateUserRequest = new UpdateUser();
        updateUserRequest.setFirstName("Jane");

        User mockUser = new User();
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(userService.updateUser(mockUser, updateUserRequest)).thenThrow(new RuntimeException("Update error"));

        ResponseEntity<UpdateUser> response = userController.updateMe(updateUserRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}