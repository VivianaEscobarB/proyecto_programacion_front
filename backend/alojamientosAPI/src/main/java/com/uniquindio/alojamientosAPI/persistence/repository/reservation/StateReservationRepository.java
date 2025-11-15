package com.uniquindio.alojamientosAPI.persistence.repository.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateReservationRepository extends JpaRepository<StateReservationEntity, Long> {
    Optional<StateReservationEntity> findByName(String name);
}

