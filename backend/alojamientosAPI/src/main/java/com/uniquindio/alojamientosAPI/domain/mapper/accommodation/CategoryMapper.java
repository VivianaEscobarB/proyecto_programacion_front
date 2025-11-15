package com.uniquindio.alojamientosAPI.domain.mapper.accommodation;

import java.util.List;

import org.mapstruct.Mapper;

import com.uniquindio.alojamientosAPI.domain.dto.accommodation.CategoryDTO;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(CategoryEntity entity);
    List<CategoryDTO> toDtoList(List<CategoryEntity> entityList);
}
