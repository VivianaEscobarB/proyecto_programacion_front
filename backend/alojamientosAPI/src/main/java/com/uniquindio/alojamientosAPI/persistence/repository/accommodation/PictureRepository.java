package com.uniquindio.alojamientosAPI.persistence.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.PictureEntity;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<PictureEntity, Long> {

    // Obtener imágenes por alojamiento
    List<PictureEntity> findByAccommodationId(Long accommodationId);

    // Obtener imagen principal del alojamiento
    PictureEntity findByAccommodationIdAndIsMainTrue(Long accommodationId);
}

