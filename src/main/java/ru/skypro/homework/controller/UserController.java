package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.CurrentUserService;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Schema(description = "Контроллер для работы с пользователями")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class UserController {
    private final UserService userService;
    private final CurrentUserService currentUserService;

    @PostMapping("/set_password")
    @Operation(summary = "Set new password", description = "Обновление пароля",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<Void> setNewPassword(@RequestBody NewPassword newPassword) {
        try {
            userService.updatePassword(currentUserService.getCurrentUser(), newPassword.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при обновлении пароля", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<UserDTO> getMe() {
        try {
            UserDTO userDTO = userService.getCurrentUserDTO();
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.error("Ошибка при получении информации о пользователе", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/me")
    @Operation(summary = "Update user", description = "Обновление информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public ResponseEntity<UpdateUser> updateMe(@RequestBody UpdateUser updateUser) {
        try {
            UpdateUser updatedUser = userService.updateUser(currentUserService.getCurrentUser(), updateUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Ошибка при обновлении информации о пользователе", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user image", description = "Обновление аватара авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    public ResponseEntity<Void> updateImage(@RequestParam("image") MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                log.warn("Попытка загрузить пустое изображение");
                return ResponseEntity.badRequest().build();
            }

            log.info("Обновление аватара пользователя: {} ({} bytes)",
                    image.getOriginalFilename(), image.getSize());

            userService.updateUserImage(image);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации изображения: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Ошибка при обновлении аватара пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}