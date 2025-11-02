package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.service.AdService;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Schema(description = "Объявления")
public class AdController {
    private final AdService adService;





    @GetMapping
    @Operation(summary = "Get all ads", description = "получение всех объявлений")
    public Ads getAllAds() {
        return new Ads();
    }

    @PostMapping
    @Operation(summary = "Add new ad", description = "добавление нового объявления",
            parameters = {
                    @Parameter(name = "ad", description = "new ad to add"),
                    @Parameter(name = "image", description = "image for new ad")
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized") })
    public Ad addAd(@RequestBody CreateOrUpdateAd ad, @RequestBody String image) {
        return new Ad();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ad by id", description = "получение объявления по id",
            parameters = @Parameter(name = "id", description = "id of ad to get"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    public Ad getAd(@PathVariable int id) {
        return new Ad();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ad by id", description = "удаление объявления по id",
            parameters = @Parameter(name = "id", description = "id of ad to delete"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    public void deleteAd(@PathVariable int id) {
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update ad by id", description = "обновление объявления по id",
            parameters = {@Parameter(name = "id", description = "id of ad to update")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    public CreateOrUpdateAd updateAd(@PathVariable int id) {
        return new CreateOrUpdateAd();
    }

    @GetMapping("/me")
    @Operation(summary = "Get my ads", description = "получение объявлений автора",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public Ads getMyAds() {
        return new Ads();
    }

    @PatchMapping("/{id}/image")
    @Operation(summary = "Update image of ad", description = "обновление картинки объявления",
            parameters = {
                    @Parameter(name = "id", description = "id of ad to update", required = true),
                    @Parameter(name = "image", description = "new image for ad", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    public void updateImage(@PathVariable int id, @RequestBody String image) {
    }
}