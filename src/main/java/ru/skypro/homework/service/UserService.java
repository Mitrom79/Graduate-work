package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;

public interface UserService {

    void updatePassword(User user, String newPassword);

    UserDTO getUserDTO(User user);

    UpdateUser updateUser(User user, UpdateUser updateUser);

    void updateUserImage(MultipartFile image);
    String updateUserImage(User user, MultipartFile image);

    UserDTO getCurrentUserDTO();
}