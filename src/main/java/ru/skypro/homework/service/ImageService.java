package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${image.ads.directory:ads-images}")
    private String adsImageDir;

    @Value("${image.users.directory:users-avatars}")
    private String usersImageDir;

    private final AdRepository adRepository;
    private final UserRepository userRepository;


    public String saveAdImage(MultipartFile image) throws IOException {
        String filename = generateFileName(image);
        Path filePath = Path.of(adsImageDir, filename);
        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Изображение объявления сохранено: {}", filename);
        return "/images/ads/" + filename;
    }


    public String saveUserImage(MultipartFile image) throws IOException {
        String filename = generateFileName(image);
        Path filePath = Path.of(usersImageDir, filename);
        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Аватар пользователя сохранен: {}", filename);
        return "/images/users/" + filename;
    }


    public byte[] getAdImage(String filename) throws IOException {
        Path filePath = Path.of(adsImageDir, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("Изображение не найдено: " + filename);
        }
        return Files.readAllBytes(filePath);
    }


    public byte[] getUserImage(String filename) throws IOException {
        Path filePath = Path.of(usersImageDir, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("Аватар не найден: " + filename);
        }
        return Files.readAllBytes(filePath);
    }


    public void deleteAdImage(String imagePath) throws IOException {
        if (imagePath != null && imagePath.startsWith("/images/ads/")) {
            String filename = imagePath.substring("/images/ads/".length());
            Path filePath = Path.of(adsImageDir, filename);
            Files.deleteIfExists(filePath);
            log.info("Изображение объявления удалено: {}", filename);
        }
    }


    public void deleteUserImage(String imagePath) throws IOException {
        if (imagePath != null && imagePath.startsWith("/images/users/")) {
            String filename = imagePath.substring("/images/users/".length());
            Path filePath = Path.of(usersImageDir, filename);
            Files.deleteIfExists(filePath);
            log.info("Аватар пользователя удален: {}", filename);
        }
    }


    public String updateAdImage(String oldImagePath, MultipartFile newImage) throws IOException {
        if (oldImagePath != null) {
            deleteAdImage(oldImagePath);
        }
        return saveAdImage(newImage);
    }


    public String updateUserImage(String oldImagePath, MultipartFile newImage) throws IOException {
        if (oldImagePath != null) {
            deleteUserImage(oldImagePath);
        }
        return saveUserImage(newImage);
    }

    private String generateFileName(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
