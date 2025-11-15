package com.uniquindio.alojamientosAPI.persistence.repository.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationGuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationGuestRepository extends JpaRepository<ReservationGuestEntity, Long> {

    List<ReservationGuestEntity> findByReservation_Id(Long reservationId);
}

