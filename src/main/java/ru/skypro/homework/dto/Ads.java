package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class Ads {
    @Schema(description = "Список объявлений")
    private List<Ad> results;
}