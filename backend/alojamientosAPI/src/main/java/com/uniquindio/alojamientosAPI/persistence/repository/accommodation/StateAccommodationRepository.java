package com.uniquindio.alojamientosAPI.persistence.repository.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.StateAccommodationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateAccommodationRepository extends JpaRepository<StateAccommodationEntity, Long> {
    Optional<StateAccommodationEntity> findByName(String name);
}
