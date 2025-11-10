package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
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

    public AdDTO addAd(CreateOrUpdateAd createAd, MultipartFile image) {
        User author = currentUserService.getCurrentUser();

        Ad ad = new Ad();
        ad.setAuthor(author.getId());
        ad.setTitle(createAd.getTitle());
        ad.setPrice(createAd.getPrice());
        ad.setDescription(createAd.getDescription());

        try {
            String imageString = "data:image/jpeg;base64," +
                    java.util.Base64.getEncoder().encodeToString(image.getBytes());
            ad.setImage(imageString);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }

        Ad savedAd = adRepository.save(ad);
        return adMapper.adToAdDto(savedAd);
    }

    public void deleteAd(int id) {
        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (ad.getAuthor() != currentUser.getId() && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав для удаления этого объявления");
        }

        adRepository.delete(ad);
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

    public void updateImage(int id, MultipartFile image) {
        User currentUser = currentUserService.getCurrentUser();
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (ad.getAuthor() != currentUser.getId() && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав для редактирования этого объявления");
        }

        try {
            String imageString = "data:image/jpeg;base64," +
                    java.util.Base64.getEncoder().encodeToString(image.getBytes());
            ad.setImage(imageString);
            adRepository.save(ad);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки изображения", e);
        }
    }
}