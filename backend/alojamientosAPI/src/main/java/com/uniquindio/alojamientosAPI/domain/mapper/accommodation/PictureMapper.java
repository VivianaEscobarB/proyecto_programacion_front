package com.uniquindio.alojamientosAPI.domain.mapper.accommodation;

import com.uniquindio.alojamientosAPI.domain.dto.accommodation.PictureDTO;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.PictureEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PictureMapper {
    PictureDTO toDto(PictureEntity entity);
    PictureEntity toEntity(PictureDTO dto);
    List<PictureDTO> toDtoList(List<PictureEntity> entities);
}
