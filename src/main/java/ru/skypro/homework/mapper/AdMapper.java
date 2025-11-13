package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.Ad;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(source = "author", target = "author")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "pk", target = "pk")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    AdDTO adToAdDto(Ad ad);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "image", ignore = true)
    Ad createOrUpdateAdToAd(CreateOrUpdateAd createOrUpdateAd);

    default Ad toAd(CreateOrUpdateAd createOrUpdateAd) {
        if (createOrUpdateAd == null) {
            return null;
        }
        Ad ad = new Ad();
        ad.setTitle(createOrUpdateAd.getTitle());
        ad.setPrice(createOrUpdateAd.getPrice());
        ad.setDescription(createOrUpdateAd.getDescription());
        return ad;
    }
}