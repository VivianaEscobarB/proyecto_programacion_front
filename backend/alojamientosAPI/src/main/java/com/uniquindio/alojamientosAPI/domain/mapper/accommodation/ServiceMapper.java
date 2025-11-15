package com.uniquindio.alojamientosAPI.domain.mapper.accommodation;

import org.mapstruct.Mapper;
import com.uniquindio.alojamientosAPI.domain.dto.accommodation.ServiceDTO;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.ServiceEntity;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceDTO toDto(ServiceEntity entity);
    ServiceEntity toEntity(ServiceDTO dto);
    List<ServiceDTO> toDtoList(List<ServiceEntity> entities);
}
