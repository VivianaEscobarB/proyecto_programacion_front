package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;

import java.util.List;
import java.util.Optional;

public interface AccommodationService {

    AccommodationEntity create(AccommodationEntity entity);

    AccommodationEntity update(Long id, AccommodationEntity entity);

    void delete(Long id);

    List<AccommodationEntity> listAll();

    Optional<AccommodationEntity> findById(Long id);

    List<AccommodationEntity> findByCity(Long cityId);

    List<AccommodationEntity> findByHost(Long hostUserId);

    List<AccommodationEntity> findByState(Long stateId);

    AccommodationEntity updateState(Long id, String stateName);

    default AccommodationEntity activate(Long id) {
        return updateState(id, "Activo");
    }

    default AccommodationEntity deactivate(Long id) {
        return updateState(id, "Inactivo");
    }
}
