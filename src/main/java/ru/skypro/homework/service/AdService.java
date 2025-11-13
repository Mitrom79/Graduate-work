package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {

    @Value("${path.to.ads.folder:ads}")
    private String adDir;

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AdMapper adMapper;

    public Ads getAllAds() {
        try {
            List<Ad> ads = adRepository.findAll();
            List<AdDTO> adDTOs = ads.stream()
                    .map(adMapper::adToAdDto)
                    .collect(Collectors.toList());

            Ads result = new Ads();
            result.setResults(adDTOs);
            log.info("Получено {} объявлений", adDTOs.size());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при получении всех объявлений", e);
            throw new RuntimeException("Не удалось получить объявления", e);
        }
    }

    public AdDTO getAd(int id) {
        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Объявление с id " + id + " не найдено"));
            return adMapper.adToAdDto(ad);
        } catch (Exception e) {
            log.error("Ошибка при получении объявления с id {}", id, e);
            throw new RuntimeException("Не удалось получить объявление", e);
        }
    }

    public AdDTO addAd(CreateOrUpdateAd properties, MultipartFile image) throws IOException {
        try {
            User author = currentUserService.getCurrentUser();
            log.info("Создание объявления для пользователя: {}", author.getEmail());

            // Валидация данных
            if (properties.getTitle() == null || properties.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Заголовок объявления не может быть пустым");
            }
            if (properties.getPrice() <= 0) {
                throw new IllegalArgumentException("Цена должна быть положительной");
            }
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Изображение обязательно");
            }

            int nextId = getNextAdId();

            Ad ad = adMapper.toAd(properties);
            ad.setAuthor(author.getId());
            ad.setPk(nextId);

            // Сохранение изображения
            String filename = generateFileName(author.getEmail(), image);
            Path filePath = Path.of(adDir, filename);
            Files.createDirectories(filePath.getParent());
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ad.setImage("/" + adDir + "/" + filename); // Сохраняем относительный путь

            Ad savedAd = adRepository.save(ad);
            log.info("Объявление успешно создано: id={}, title={}, пользователь {}",
                    savedAd.getPk(), savedAd.getTitle(), author.getEmail());

            return adMapper.adToAdDto(savedAd);
        } catch (Exception e) {
            log.error("Ошибка при создании объявления", e);
            throw e; // Пробрасываем исключение дальше
        }
    }

    public void deleteAd(int id) {
        try {
            User currentUser = currentUserService.getCurrentUser();
            Ad ad = adRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Объявление с id " + id + " не найдено"));

            // Проверка прав
            if (ad.getAuthor() != currentUser.getId() && currentUser.getRole() != Role.ADMIN) {
                throw new SecurityException("Недостаточно прав для удаления этого объявления");
            }

            // Удаление файла изображения
            if (ad.getImage() != null && !ad.getImage().startsWith("data:")) {
                try {
                    Path imagePath = Path.of(ad.getImage()).normalize();
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    log.warn("Не удалось удалить файл изображения: {}", ad.getImage(), e);
                }
            }

            adRepository.delete(ad);
            log.info("Объявление {} удалено пользователем {}", id, currentUser.getEmail());
        } catch (Exception e) {
            log.error("Ошибка при удалении объявления с id {}", id, e);
            throw new RuntimeException("Не удалось удалить объявление", e);
        }
    }

    public CreateOrUpdateAd updateAd(int id, CreateOrUpdateAd updateAd) {
        try {
            User currentUser = currentUserService.getCurrentUser();
            Ad ad = adRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Объявление с id " + id + " не найдено"));

            // Проверка прав
            if (ad.getAuthor() != currentUser.getId() && currentUser.getRole() != Role.ADMIN) {
                throw new SecurityException("Недостаточно прав для редактирования этого объявления");
            }

            // Валидация
            if (updateAd.getTitle() == null || updateAd.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Заголовок объявления не может быть пустым");
            }
            if (updateAd.getPrice() <= 0) {
                throw new IllegalArgumentException("Цена должна быть положительной");
            }

            ad.setTitle(updateAd.getTitle());
            ad.setPrice(updateAd.getPrice());
            ad.setDescription(updateAd.getDescription());
            adRepository.save(ad);

            log.info("Объявление {} обновлено пользователем {}", id, currentUser.getEmail());
            return updateAd;
        } catch (Exception e) {
            log.error("Ошибка при обновлении объявления с id {}", id, e);
            throw new RuntimeException("Не удалось обновить объявление", e);
        }
    }

    public Ads getMyAds() {
        try {
            User currentUser = currentUserService.getCurrentUser();
            List<Ad> userAds = adRepository.findByAuthor(currentUser.getId());

            List<AdDTO> adDTOs = userAds.stream()
                    .map(adMapper::adToAdDto)
                    .collect(Collectors.toList());

            Ads result = new Ads();
            result.setResults(adDTOs);
            log.info("Получено {} объявлений пользователя {}", adDTOs.size(), currentUser.getEmail());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при получении объявлений пользователя", e);
            throw new RuntimeException("Не удалось получить ваши объявления", e);
        }
    }

    public void updateImage(int id, MultipartFile image) throws IOException {
        try {
            User currentUser = currentUserService.getCurrentUser();
            Ad ad = adRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Объявление с id " + id + " не найдено"));

            if (ad.getAuthor() != currentUser.getId() && currentUser.getRole() != Role.ADMIN) {
                throw new SecurityException("Недостаточно прав для редактирования этого объявления");
            }

            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Изображение обязательно");
            }

            updateAdImageInternal(ad, image, currentUser);
        } catch (Exception e) {
            log.error("Ошибка при обновлении изображения объявления с id {}", id, e);
            throw e;
        }
    }

    private void updateAdImageInternal(Ad ad, MultipartFile imageFile, User author) throws IOException {
        String newFileName = generateFileName(author.getEmail(), imageFile);
        Path baseDir = Path.of(adDir).toAbsolutePath().normalize();
        Path newFilePath = baseDir.resolve(newFileName).normalize();

        if (!newFilePath.startsWith(baseDir)) {
            throw new SecurityException("Недопустимый путь к файлу: попытка обхода директории");
        }

        Files.createDirectories(newFilePath.getParent());
        Files.copy(imageFile.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Удаляем старое изображение
        if (ad.getImage() != null && !ad.getImage().startsWith("data:")) {
            try {
                Path oldImagePath = Path.of(ad.getImage()).normalize();
                Files.deleteIfExists(oldImagePath);
            } catch (IOException e) {
                log.warn("Не удалось удалить старое изображение: {}", ad.getImage(), e);
            }
        }

        ad.setImage("/" + adDir + "/" + newFileName);
        adRepository.save(ad);
        log.info("Изображение объявления {} обновлено", ad.getPk());
    }

    private String generateFileName(String email, MultipartFile image) {
        String extension = getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        return email + "_" + System.currentTimeMillis() + "." + extension;
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "jpg"; // расширение по умолчанию
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private int getNextAdId() {
        return adRepository.findAll().stream()
                .mapToInt(Ad::getPk)
                .max()
                .orElse(0) + 1;
    }
}