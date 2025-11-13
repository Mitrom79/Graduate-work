package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.service.AdService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/ads")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS }
)
@Schema(description = "Объявления")
public class AdController {
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    @Operation(summary = "Get all ads", description = "получение всех объявлений")
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add new ad", description = "добавление нового объявления",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Some fields haven't passed validation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error") })
    public ResponseEntity<AdDTO> addAd(@RequestPart CreateOrUpdateAd ad,
                                       @RequestPart MultipartFile image) {
        try {
            AdDTO createdAd = adService.addAd(ad, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения объявления", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ad by id", description = "получение объявления по id",
            parameters = @Parameter(name = "id", description = "id of ad to get"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    public ResponseEntity<AdDTO> getAd(@PathVariable int id) {
        return ResponseEntity.ok(adService.getAd(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ad by id", description = "удаление объявления по id",
            parameters = @Parameter(name = "id", description = "id of ad to delete"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")})
    public ResponseEntity<Void> deleteAd(@PathVariable int id) {
        adService.deleteAd(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<CreateOrUpdateAd> updateAd(@PathVariable int id, @RequestBody CreateOrUpdateAd updateAd) {
        CreateOrUpdateAd updatedAd = adService.updateAd(id, updateAd);
        return ResponseEntity.ok(updatedAd);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my ads", description = "получение объявлений автора",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<Ads> getMyAds() {
        return ResponseEntity.ok(adService.getMyAds());
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update image of ad", description = "обновление картинки объявления",
            parameters = {
                    @Parameter(name = "id", description = "id of ad to update", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Void> updateImage(@PathVariable int id, @RequestPart MultipartFile image) {
        try {
            adService.updateImage(id, image);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Ошибка при обновлении изображения объявления", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}