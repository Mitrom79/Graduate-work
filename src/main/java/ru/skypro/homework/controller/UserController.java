package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Schema(description = "Контроллер для работы с пользователями")
public class UserController {
    private final UserService userService;

    @PatchMapping("/set_password")
    @Operation(summary = "Set new password", description = "Обновление пароля",
            parameters = @Parameter(name = "newPassword", description = "new password"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public void setNewPassword(@RequestBody NewPassword newPassword){
    }

    @GetMapping("/me")
    @Operation(summary = "Get user by id", description = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public User getMe(){
        return new User();
    }

    @PatchMapping("/me")
    @Operation(summary = "Update user", description = "Обновление информации об авторизованном пользователе",
            parameters = @Parameter(name = "updateUser", description = "new user info"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public UpdateUser updateMe(@RequestBody UpdateUser updateUser){
        return new UpdateUser();
    }

    @PatchMapping("/me/image")
    @Operation(summary = "Update user image", description = "Обновление аватара авторизованного пользователя",
            parameters = @Parameter(name = "image", description = "new user image"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    public void updateImage(@PathVariable String image){
    }

}
