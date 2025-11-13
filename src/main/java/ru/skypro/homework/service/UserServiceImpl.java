package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;
    private final ImageService imageService;

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return userMapper.userToUserDto(user);
    }

    @Override
    public UpdateUser updateUser(User user, UpdateUser updateUser) {
        if (user == null || updateUser == null) {
            return null;
        }

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);

        UpdateUser response = new UpdateUser();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        return response;
    }


    @Override
    public void updateUserImage(MultipartFile image) {
        User user = currentUserService.getCurrentUser();
        try {
            if (image != null && !image.isEmpty()) {
                String imagePath = imageService.saveUserImage(image);
                user.setImage(imagePath);
                userRepository.save(user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }
    }

    @Override
    public String updateUserImage(User user, MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                String imagePath = imageService.saveUserImage(image);
                user.setImage(imagePath);
                userRepository.save(user);
                return imagePath;
            }
            return user.getImage();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }
    }

    @Override
    public UserDTO getCurrentUserDTO() {
        User user = currentUserService.getCurrentUser();
        return getUserDTO(user);
    }
}