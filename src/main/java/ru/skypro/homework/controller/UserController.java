package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.CurrentUserService;

@RestController
@RequestMapping("/users")
@Schema(description = "Контроллер для работы с пользователями")
public class UserController {
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public UserController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/set_password")
    @Operation(summary = "Set new password", description = "Обновление пароля",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<Void> setNewPassword(@RequestBody NewPassword newPassword) {
        userService.updatePassword(currentUserService.getCurrentUser(), newPassword.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<UserDTO> getMe() {
        UserDTO userDTO = userService.getCurrentUserDTO();
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update user", description = "Обновление информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<UpdateUser> updateMe(@RequestBody UpdateUser updateUser) {
        UpdateUser updatedUser = userService.updateUser(currentUserService.getCurrentUser(), updateUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user image", description = "Обновление аватара авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<Void> updateImage(@RequestParam("image") MultipartFile image) {
        userService.updateUserImage(image);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/me/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(
            summary = "Get current user image",
            description = "Получение аватара авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Изображение получено",
                            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "image/jpeg")),
                    @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<byte[]> getMyImage() {
        byte[] image = userService.getUserImage();
        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(
            summary = "Get user image by ID",
            description = "Получение аватара пользователя по идентификатору",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID пользователя",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Изображение получено",
                            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "image/jpeg")),
                    @ApiResponse(responseCode = "404", description = "Пользователь или изображение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<byte[]> getUserImage(@PathVariable Integer id) {
        byte[] image = userService.getUserImage(id);
        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }
}