package com.uniquindio.alojamientosAPI.domain.service.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationEntity createReservation(Long userId,
                                        Long accommodationId,
                                        LocalDate checkIn,
                                        LocalDate checkOut,
                                        Integer countRoommates);

    void cancelReservation(Long reservationId, Long userId);

    List<ReservationEntity> listByUser(Long userId);

    // Nuevos para anfitrión
    List<ReservationEntity> listByHost(Long hostUserId);

    List<ReservationEntity> listByHostFiltered(Long hostUserId, String state);

    ReservationEntity approveReservation(Long reservationId, Long hostUserId);

    ReservationEntity rejectReservation(Long reservationId, Long hostUserId);

    void deleteReservation(Long reservationId, Long actorUserId);

    ReservationEntity finalizeReservation(Long reservationId, Long hostUserId);
}
