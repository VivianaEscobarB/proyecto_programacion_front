package com.uniquindio.alojamientosAPI.web.reservation;

import com.uniquindio.alojamientosAPI.domain.dto.reservation.ReservationCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.reservation.ReservationResponse;
import com.uniquindio.alojamientosAPI.domain.mapper.reservation.ReservationMapper;
import com.uniquindio.alojamientosAPI.domain.service.reservation.ReservationService;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/reservations")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        Long userId = extractUserId(principal);
        log.info("[RESERVATION][CREATE] userId={}, email={}, roles={}", userId, principal.getUsername(), principal.getAuthorities());
        try {
            ReservationEntity saved = reservationService.createReservation(
                    userId,
                    request.getAccommodationId(),
                    request.getCheckIn(),
                    request.getCheckOut(),
                    request.getCountRoommates()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(ReservationMapper.toResponse(saved));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalStateException e) {
            // Conflicto por disponibilidad u otras reglas de negocio
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ANFITRION')")
    public ResponseEntity<List<ReservationResponse>> myReservations(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long userId = extractUserId(principal);
        log.debug("[RESERVATION][LIST_MINE] userId={}, email={}, roles={}", userId, principal.getUsername(), principal.getAuthorities());
        List<ReservationResponse> list = reservationService.listByUser(userId)
                .stream()
                .map(ReservationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{reservationId}/cancel")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ANFITRION')")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        Long userId = extractUserId(principal);
        log.info("[RESERVATION][CANCEL] reservationId={}, userId={}, email={}", reservationId, userId, principal.getUsername());
        try {
            reservationService.cancelReservation(reservationId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            // No autorizado o regla que impide cancelar
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @GetMapping("/host/me")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<List<ReservationResponse>> hostReservations(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        Long hostId = extractUserId(principal);
        log.debug("[RESERVATION][HOST_LIST] hostId={}, state={}, page={}, size={}", hostId, state, page, size);
        List<ReservationResponse> all = reservationService.listByHostFiltered(hostId, state)
                .stream().map(ReservationMapper::toResponse).collect(Collectors.toList());
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return ResponseEntity.ok(all.subList(from, to));
    }

    @PostMapping("/{reservationId}/approve")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ReservationResponse> approve(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        Long hostId = extractUserId(principal);
        log.info("[RESERVATION][APPROVE] reservationId={}, hostId={}", reservationId, hostId);
        ReservationEntity updated = reservationService.approveReservation(reservationId, hostId);
        return ResponseEntity.ok(ReservationMapper.toResponse(updated));
    }

    @PostMapping("/{reservationId}/reject")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ReservationResponse> reject(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        Long hostId = extractUserId(principal);
        log.info("[RESERVATION][REJECT] reservationId={}, hostId={}", reservationId, hostId);
        ReservationEntity updated = reservationService.rejectReservation(reservationId, hostId);
        return ResponseEntity.ok(ReservationMapper.toResponse(updated));
    }

    @PostMapping("/{reservationId}/finalize")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ReservationResponse> finalizeReservation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        Long hostId = extractUserId(principal);
        ReservationEntity updated = reservationService.finalizeReservation(reservationId, hostId);
        return ResponseEntity.ok(ReservationMapper.toResponse(updated));
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ANFITRION')")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long reservationId
    ) {
        Long userId = extractUserId(principal);
        try {
            reservationService.deleteReservation(reservationId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    private Long extractUserId(CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null || principal.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return principal.getUser().getId();
    }
}
