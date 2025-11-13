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
        List<Ad> ads = adRepository.findAll();
        List<AdDTO> adDTOs = ads.stream()
                .map(adMapper::adToAdDto)
                .collect(Collectors.toList());

        Ads result = new Ads();
        result.setResults(adDTOs);
        return result;
    }

    public AdDTO getAd(int id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
        return adMapper.adToAdDto(ad);
    }

    public AdDTO addAd(CreateOrUpdateAd createAd, MultipartFile image) throws IOException {
        User author = currentUserService.getCurrentUser();
        log.debug("Creating ad for user: {}", author.getEmail());


        int nextId = getNextAdId();

        Ad ad = new Ad();
        ad.setAuthor(author.getId());
        ad.setPk(nextId);
        ad.setTitle(createAd.getTitle());
        ad.setPrice(createAd.getPrice());
        ad.setDescription(createAd.getDescription());


        String filename = author.getEmail() + "_" + System.currentTimeMillis() + "." + getExtension(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Path.of(adDir, filename);
        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath);


        ad.setImage(filePath.toString());

        Ad savedAd = adRepository.save(ad);
        log.info("Ad created successfully by user {}", author.getEmail());
        return adMapper.adToAdDto(savedAd);
    }

    public void deleteAd(int id) {
        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (ad.getAuthor() != currentUser.getId() && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав для удаления этого объявления");
        }


        if (ad.getImage() != null && !ad.getImage().startsWith("data:")) {
            try {
                Files.deleteIfExists(Path.of(ad.getImage()));
            } catch (IOException e) {
                log.error("Failed to delete ad image file: {}", ad.getImage(), e);
            }
        }

        adRepository.delete(ad);
        log.info("Ad {} deleted successfully by user {}", id, currentUser.getEmail());
    }

    public CreateOrUpdateAd updateAd(int id, CreateOrUpdateAd updateAd) {
        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (ad.getAuthor() != currentUser.getId() && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав для редактирования этого объявления");
        }

        ad.setTitle(updateAd.getTitle());
        ad.setPrice(updateAd.getPrice());
        ad.setDescription(updateAd.getDescription());
        adRepository.save(ad);

        log.info("Ad {} updated successfully by user {}", id, currentUser.getEmail());
        return updateAd;
    }

    public Ads getMyAds() {
        User currentUser = currentUserService.getCurrentUser();
        List<Ad> userAds = adRepository.findByAuthor(currentUser.getId());

        List<AdDTO> adDTOs = userAds.stream()
                .map(adMapper::adToAdDto)
                .collect(Collectors.toList());

        Ads result = new Ads();
        result.setResults(adDTOs);
        return result;
    }

    public void updateImage(int id, MultipartFile image) throws IOException {
        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (ad.getAuthor() != currentUser.getId() && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав для редактирования этого объявления");
        }

        updateAdImageInternal(ad, image, currentUser);
    }


    private void updateAdImageInternal(Ad ad, MultipartFile imageFile, User author) throws IOException {
        String extension = getExtension(Objects.requireNonNull(imageFile.getOriginalFilename()));
        String newFileName = author.getEmail() + "_" + System.currentTimeMillis() + "." + extension;

        Path baseDir = Path.of(adDir).toAbsolutePath().normalize();
        Path newFilePath = baseDir.resolve(newFileName).normalize();

        if (!newFilePath.startsWith(baseDir)) {
            throw new SecurityException("Invalid file path: attempted path traversal");
        }

        Files.createDirectories(newFilePath.getParent());
        Files.copy(imageFile.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);


        if (ad.getImage() != null && !ad.getImage().startsWith("data:") && !ad.getImage().equals(newFilePath.toString())) {
            try {
                Files.deleteIfExists(Path.of(ad.getImage()));
            } catch (IOException e) {
                log.error("Failed to delete old ad image: {}", ad.getImage(), e);
            }
        }

        ad.setImage(newFilePath.toString());
        adRepository.save(ad);
        log.info("Ad image updated");
    }


    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }


    private int getNextAdId() {
        return adRepository.findAll().stream()
                .mapToInt(Ad::getPk)
                .max()
                .orElse(0) + 1;
    }
}