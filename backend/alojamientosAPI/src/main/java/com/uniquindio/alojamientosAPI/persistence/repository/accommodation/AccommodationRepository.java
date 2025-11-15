package com.uniquindio.alojamientosAPI.persistence.repository.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long> {

    // ⚠️ Evitamos cargar dos bags simultáneamente (pictures + services) para prevenir MultipleBagFetchException.
    @EntityGraph(attributePaths = {"stateAccommodation","city","hostUser","pictures"})
    List<AccommodationEntity> findByCityId(Long cityId);

    @EntityGraph(attributePaths = {"stateAccommodation","city","hostUser","pictures"})
    List<AccommodationEntity> findByHostUserId(Long hostUserId);

    @EntityGraph(attributePaths = {"stateAccommodation","city","hostUser","pictures"})
    List<AccommodationEntity> findByStateAccommodationId(Long stateAccommodationId);

    // Cargar todas las relaciones necesarias para listado completo
    @Override
    @EntityGraph(attributePaths = {"stateAccommodation","city","hostUser","pictures"})
    List<AccommodationEntity> findAll();

    // Cargar relaciones al buscar por id (detalle)
    @Override
    @EntityGraph(attributePaths = {"stateAccommodation","city","hostUser","pictures"})
    Optional<AccommodationEntity> findById(Long id);
}
