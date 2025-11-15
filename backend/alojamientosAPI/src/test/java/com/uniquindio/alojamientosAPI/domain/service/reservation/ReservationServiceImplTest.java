package com.uniquindio.alojamientosAPI.domain.service.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.AccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.StateReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    StateReservationRepository stateReservationRepository;
    @Mock
    AccommodationRepository accommodationRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    ReservationServiceImpl service;

    private UserEntity user;
    private AccommodationEntity accommodation;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(1L);

        accommodation = AccommodationEntity.builder()
                .id(10L)
                .capacity(4)
                .build();
    }

    @Test
    void createReservation_success_backToBackAllowed() {
        // Given
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodation_IdAndCheckInLessThanAndCheckOutGreaterThan(10L, out, in))
                .thenReturn(false);
        when(stateReservationRepository.findByName("Confirmada"))
                .thenReturn(Optional.of(StateReservationEntity.builder().id(100L).name("Confirmada").build()));
        when(reservationRepository.save(any(ReservationEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0, ReservationEntity.class));

        // When
        ReservationEntity created = service.createReservation(1L, 10L, in, out, 2);

        // Then
        assertThat(created).isNotNull();
        assertEquals(in, created.getCheckIn());
        assertEquals(out, created.getCheckOut());
        assertEquals(2, created.getCountRoommates());
        verify(reservationRepository).save(any(ReservationEntity.class));
    }

    @Test
    void createReservation_overlap_throws() {
        // Given
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodation_IdAndCheckInLessThanAndCheckOutGreaterThan(10L, out, in))
                .thenReturn(true);

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> service.createReservation(1L, 10L, in, out, 2));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_authorized_changesStateToCanceled() {
        // Given
        ReservationEntity res = ReservationEntity.builder()
                .id(99L)
                .user(user)
                .state(StateReservationEntity.builder().name("Confirmada").build())
                .build();

        when(reservationRepository.findById(99L)).thenReturn(Optional.of(res));
        when(stateReservationRepository.findByName("Cancelada"))
                .thenReturn(Optional.of(StateReservationEntity.builder().id(101L).name("Cancelada").build()));

        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);

        // When
        service.cancelReservation(99L, 1L);

        // Then
        verify(reservationRepository).save(captor.capture());
        assertEquals("Cancelada", captor.getValue().getState().getName());
    }

    @Test
    void cancelReservation_unauthorized_throws() {
        // Given
        UserEntity other = new UserEntity();
        other.setId(2L);
        ReservationEntity res = ReservationEntity.builder()
                .id(99L)
                .user(other)
                .state(StateReservationEntity.builder().name("Confirmada").build())
                .build();

        when(reservationRepository.findById(99L)).thenReturn(Optional.of(res));

        // When / Then
        assertThrows(IllegalStateException.class, () -> service.cancelReservation(99L, 1L));
        verify(reservationRepository, never()).save(any());
    }
}

