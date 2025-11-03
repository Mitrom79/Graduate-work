package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.entity.ExtendedAd;

@Mapper
public interface ExtendedAdMapper {
    ExtendedAdMapper INSTANCE = Mappers.getMapper(ExtendedAdMapper.class);

    ExtendedAdDTO extendedAdToExtendedAdDto(ExtendedAd extendedAd);

    ExtendedAd extendedAdDtoToExtendedAd(ExtendedAdDTO extendedAdDTO);
}
