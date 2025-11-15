package com.uniquindio.alojamientosAPI.persistence.repository.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.GuestRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRatingRepository extends JpaRepository<GuestRatingEntity, Long> {

    List<GuestRatingEntity> findByReservation_Id(Long reservationId);

    List<GuestRatingEntity> findByUser_Id(Long userId);

    Optional<GuestRatingEntity> findByUser_IdAndReservation_Id(Long userId, Long reservationId);
}

