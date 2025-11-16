package com.uniquindio.alojamientosAPI.persistence.repository.accommodation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.ServiceEntity;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    // Buscar servicios por alojamiento
    List<ServiceEntity> findByAccommodationId(Long accommodationId);

    // Buscar servicios por categoría
    List<ServiceEntity> findByCategoryId(Long categoryId);
}
