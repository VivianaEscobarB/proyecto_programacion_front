package com.uniquindio.alojamientosAPI.persistence.repository.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @EntityGraph(attributePaths = {"state","accommodation","accommodation.city","accommodation.hostUser","user"})
    List<ReservationEntity> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"state","accommodation","accommodation.city","accommodation.hostUser","user"})
    List<ReservationEntity> findByAccommodation_Id(Long accommodationId);

    // Listar todas las reservas de alojamientos cuyo anfitrión sea el indicado
    @EntityGraph(attributePaths = {"state","accommodation","accommodation.city","accommodation.hostUser","user"})
    List<ReservationEntity> findByAccommodation_HostUser_Id(Long hostUserId);

    // Filtrar por estado para un anfitrión
    @EntityGraph(attributePaths = {"state","accommodation","accommodation.city","accommodation.hostUser","user"})
    List<ReservationEntity> findByAccommodation_HostUser_IdAndState_Name(Long hostUserId, String stateName);

    // Útil para validar superposición (estricta): permite back-to-back
    boolean existsByAccommodation_IdAndCheckInLessThanAndCheckOutGreaterThan(
            Long accommodationId,
            LocalDate checkOut,
            LocalDate checkIn
    );

    // Opción inclusiva (bloquea back-to-back): se deja por si se necesita
    boolean existsByAccommodation_IdAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
            Long accommodationId,
            LocalDate checkOut,
            LocalDate checkIn
    );

    @EntityGraph(attributePaths = {"state","accommodation","accommodation.city","accommodation.hostUser","user"})
    ReservationEntity findByIdAndAccommodation_HostUser_Id(Long id, Long hostUserId);
}
