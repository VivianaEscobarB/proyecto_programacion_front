package com.uniquindio.alojamientosAPI.domain.service.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.AccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.StateReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StateReservationRepository stateReservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private static final String STATE_CONFIRMED = "Confirmada";
    private static final String STATE_CANCELED = "Cancelada";
    private static final String STATE_FINISHED = "Finalizada";
    private static final String STATE_PENDING = "Pendiente";
    private static final String STATE_APPROVED = "Aprobada";
    private static final String STATE_REJECTED = "Rechazada";

    @Override
    @Transactional
    public ReservationEntity createReservation(Long userId,
                                               Long accommodationId,
                                               LocalDate checkIn,
                                               LocalDate checkOut,
                                               Integer countRoommates) {
        // Validaciones básicas
        if (userId == null || accommodationId == null) {
            throw new IllegalArgumentException("Usuario y alojamiento son obligatorios");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Fechas de check-in y check-out son obligatorias");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("La fecha de check-in debe ser anterior a la de check-out");
        }
        if (countRoommates == null || countRoommates <= 0) {
            throw new IllegalArgumentException("La cantidad de acompañantes debe ser positiva");
        }

        // Entidades requeridas
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        AccommodationEntity accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Capacidad
        Integer capacity = accommodation.getCapacity();
        if (capacity != null && capacity > 0 && countRoommates > capacity) {
            throw new IllegalArgumentException("La cantidad de acompañantes excede la capacidad del alojamiento");
        }

        // Nueva lógica de disponibilidad: ignorar reservas en estados que NO bloquean (Cancelada, Rechazada, Finalizada)
        List<ReservationEntity> existing = reservationRepository.findByAccommodation_Id(accommodationId);
        boolean overlaps = existing.stream().anyMatch(r -> {
            String st = r.getState() != null ? r.getState().getName() : null;
            if (st != null && (STATE_CANCELED.equalsIgnoreCase(st) || STATE_REJECTED.equalsIgnoreCase(st) || STATE_FINISHED.equalsIgnoreCase(st))) {
                return false; // estados liberados
            }
            // Solapamiento estricto (permitimos back-to-back)
            return r.getCheckIn().isBefore(checkOut) && r.getCheckOut().isAfter(checkIn);
        });
        if (overlaps) {
            log.warn("[RESERVATION][CREATE][DENY_OVERLAP] accommodationId={}, userId={}, checkIn={}, checkOut={}", accommodationId, userId, checkIn, checkOut);
            throw new IllegalStateException("El alojamiento no está disponible en el rango solicitado");
        }

        // Estado inicial: 'Pendiente' si existe; de lo contrario 'Confirmada'
        StateReservationEntity initialState = stateReservationRepository.findByName(STATE_PENDING)
                .orElseGet(() -> stateReservationRepository.findByName(STATE_CONFIRMED)
                        .orElseThrow(() -> new IllegalStateException("Estado de reserva inicial no configurado")));

        // Crear y guardar
        ReservationEntity reservation = ReservationEntity.builder()
                .user(user)
                .accommodation(accommodation)
                .state(initialState)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .countRoommates(countRoommates)
                .build();

        ReservationEntity saved = reservationRepository.save(reservation);
        log.info("[RESERVATION][CREATED] id={}, userId={}, accommodationId={}, state={}, checkIn={}, checkOut={}",
                saved.getId(), userId, accommodationId, initialState.getName(), checkIn, checkOut);
        return saved;
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        if (reservationId == null || userId == null) {
            throw new IllegalArgumentException("Reserva y usuario son obligatorios");
        }

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Solo puede cancelar el propietario de la reserva
        Long ownerId = reservation.getUser() != null ? reservation.getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new IllegalStateException("No autorizado para cancelar esta reserva");
        }

        String currentState = reservation.getState() != null ? reservation.getState().getName() : null;
        if (STATE_CANCELED.equalsIgnoreCase(currentState)) {
            return; // idempotencia: ya cancelada
        }
        if (STATE_FINISHED.equalsIgnoreCase(currentState)) {
            throw new IllegalStateException("No es posible cancelar una reserva finalizada");
        }

        StateReservationEntity canceledState = stateReservationRepository.findByName(STATE_CANCELED)
                .orElseThrow(() -> new IllegalStateException("Estado de reserva 'Cancelada' no configurado en BD"));

        reservation.setState(canceledState);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> listByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Usuario es obligatorio");
        }
        return reservationRepository.findByUser_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> listByHost(Long hostUserId) {
        if (hostUserId == null) throw new IllegalArgumentException("Anfitrión es obligatorio");
        return reservationRepository.findByAccommodation_HostUser_Id(hostUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> listByHostFiltered(Long hostUserId, String state) {
        if (hostUserId == null) throw new IllegalArgumentException("Anfitrión es obligatorio");
        if (state == null || state.isBlank()) return reservationRepository.findByAccommodation_HostUser_Id(hostUserId);
        return reservationRepository.findByAccommodation_HostUser_IdAndState_Name(hostUserId, state);
    }

    @Override
    @Transactional
    public ReservationEntity approveReservation(Long reservationId, Long hostUserId) {
        ReservationEntity reservation = requireReservationOwnedByHost(reservationId, hostUserId);
        String current = reservation.getState() != null ? reservation.getState().getName() : null;
        if (STATE_APPROVED.equalsIgnoreCase(current)) return reservation; // idempotente
        if (STATE_CANCELED.equalsIgnoreCase(current) || STATE_REJECTED.equalsIgnoreCase(current)) {
            throw new IllegalStateException("La reserva ya fue cancelada o rechazada");
        }
        StateReservationEntity target = stateReservationRepository.findByName(STATE_APPROVED)
                .orElseGet(() -> {
                    log.warn("[RESERVATION][APPROVE] Estado 'Aprobada' faltante. Creando dinámicamente.");
                    return stateReservationRepository.save(StateReservationEntity.builder().name(STATE_APPROVED).description("Creado automáticamente").build());
                });
        reservation.setState(target);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservationEntity rejectReservation(Long reservationId, Long hostUserId) {
        ReservationEntity reservation = requireReservationOwnedByHost(reservationId, hostUserId);
        String current = reservation.getState() != null ? reservation.getState().getName() : null;
        if (STATE_REJECTED.equalsIgnoreCase(current) || STATE_CANCELED.equalsIgnoreCase(current)) return reservation;
        StateReservationEntity target = stateReservationRepository.findByName(STATE_REJECTED)
                .orElseGet(() -> {
                    log.warn("[RESERVATION][REJECT] Estado 'Rechazada' faltante. Creando dinámicamente.");
                    return stateReservationRepository.save(StateReservationEntity.builder().name(STATE_REJECTED).description("Creado automáticamente").build());
                });
        reservation.setState(target);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long reservationId, Long actorUserId) {
        if (reservationId == null || actorUserId == null) throw new IllegalArgumentException("Parámetros obligatorios");
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        Long ownerId = reservation.getUser() != null ? reservation.getUser().getId() : null;
        Long hostId = reservation.getAccommodation() != null && reservation.getAccommodation().getHostUser() != null ? reservation.getAccommodation().getHostUser().getId() : null;
        String state = reservation.getState() != null ? reservation.getState().getName() : null;
        boolean actorIsOwner = ownerId != null && ownerId.equals(actorUserId);
        boolean actorIsHost = hostId != null && hostId.equals(actorUserId);
        if (!actorIsOwner && !actorIsHost) {
            throw new IllegalStateException("No autorizado para eliminar esta reserva");
        }
        // Reglas: el cliente puede eliminar si está Cancelada o Rechazada; el anfitrión también si Cancelada/Rechazada/Finalizada.
        boolean eliminablePorCliente = STATE_CANCELED.equalsIgnoreCase(state) || STATE_REJECTED.equalsIgnoreCase(state);
        boolean eliminablePorHost = eliminablePorCliente || STATE_FINISHED.equalsIgnoreCase(state);
        if ((actorIsOwner && !eliminablePorCliente) || (actorIsHost && !eliminablePorHost)) {
            throw new IllegalStateException("Estado actual no permite eliminación");
        }
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional
    public ReservationEntity finalizeReservation(Long reservationId, Long hostUserId) {
        ReservationEntity reservation = requireReservationOwnedByHost(reservationId, hostUserId);
        String current = reservation.getState() != null ? reservation.getState().getName() : null;
        if (STATE_FINISHED.equalsIgnoreCase(current)) return reservation;
        if (STATE_CANCELED.equalsIgnoreCase(current) || STATE_REJECTED.equalsIgnoreCase(current)) {
            throw new IllegalStateException("No se puede finalizar una reserva cancelada o rechazada");
        }
        StateReservationEntity target = stateReservationRepository.findByName(STATE_FINISHED)
                .orElseGet(() -> stateReservationRepository.save(StateReservationEntity.builder().name(STATE_FINISHED).description("Creado automáticamente").build()));
        reservation.setState(target);
        return reservationRepository.save(reservation);
    }

    private ReservationEntity requireReservationOwnedByHost(Long reservationId, Long hostUserId) {
        if (reservationId == null || hostUserId == null) {
            throw new IllegalArgumentException("Reserva y anfitrión son obligatorios");
        }
        // Cargar con EntityGraph para evitar LazyInitialization en city, hostUser, etc.
        ReservationEntity reservation = reservationRepository.findByIdAndAccommodation_HostUser_Id(reservationId, hostUserId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reserva no encontrada o no pertenece al anfitrión");
        }
        return reservation;
    }
}
