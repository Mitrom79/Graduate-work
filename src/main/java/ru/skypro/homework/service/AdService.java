package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AdMapper adMapper;
    private final ImageService imageService;

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

    public AdDTO addAd(CreateOrUpdateAd properties, MultipartFile image) throws IOException {
        User author = currentUserService.getCurrentUser();
        log.debug("Creating ad for user: {}", author.getEmail());

        int nextId = getNextAdId();

        Ad ad = adMapper.toAd(properties);
        ad.setAuthor(author.getId());
        ad.setPk(nextId);


        String imagePath = imageService.saveAdImage(image);
        ad.setImage(imagePath);

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


        try {
            imageService.deleteAdImage(ad.getImage());
        } catch (IOException e) {
            log.error("Failed to delete ad image: {}", ad.getImage(), e);
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


        String newImagePath = imageService.updateAdImage(ad.getImage(), image);
        ad.setImage(newImagePath);
        adRepository.save(ad);

        log.info("Ad image updated for ad {}", id);
    }

    private int getNextAdId() {
        return adRepository.findAll().stream()
                .mapToInt(Ad::getPk)
                .max()
                .orElse(0) + 1;
    }
}