package com.uniquindio.alojamientosAPI.domain.service.reservation;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;
import com.uniquindio.alojamientosAPI.domain.service.review.ReviewServiceImpl;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.GuestRatingEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReviewCommentEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.AccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.GuestRatingRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReviewCommentRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.StateReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ReservationAndReviewWorkflowTest {

    // Mocks para reservas
    @Mock ReservationRepository reservationRepository;
    @Mock StateReservationRepository stateReservationRepository;
    @Mock AccommodationRepository accommodationRepository;
    @Mock UserRepository userRepository;

    // Mocks para reviews
    @Mock GuestRatingRepository guestRatingRepository;
    @Mock ReviewCommentRepository reviewCommentRepository;

    // Servicios bajo prueba
    @InjectMocks ReservationServiceImpl reservationService;
    ReviewServiceImpl reviewService;

    private UserEntity user;
    private AccommodationEntity accommodation;
    private StateReservationEntity stateConfirmada;
    private StateReservationEntity stateFinalizada;

    @BeforeEach
    void setup() {
        reviewService = new ReviewServiceImpl(reservationRepository, guestRatingRepository, reviewCommentRepository, userRepository);

        user = new UserEntity();
        user.setId(1L);

        accommodation = AccommodationEntity.builder()
                .id(10L)
                .capacity(4)
                .build();

        stateConfirmada = StateReservationEntity.builder().id(100L).name("Confirmada").build();
        stateFinalizada = StateReservationEntity.builder().id(101L).name("Finalizada").build();
    }

    @Test
    void fullWorkflow_create_finalize_rate_and_comment_success() {
        // Arrange: datos
        LocalDate in = LocalDate.now().plusDays(3);
        LocalDate out = in.plusDays(2);

        // Stubs creación reserva
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accommodationRepository.findById(10L)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodation_IdAndCheckInLessThanAndCheckOutGreaterThan(10L, out, in))
                .thenReturn(false);
        when(stateReservationRepository.findByName("Confirmada")).thenReturn(Optional.of(stateConfirmada));
        when(reservationRepository.save(any(ReservationEntity.class)))
                .thenAnswer(inv -> {
                    ReservationEntity r = inv.getArgument(0, ReservationEntity.class);
                    r.setId(99L);
                    return r;
                });

        // Act: crear reserva
        ReservationEntity created = reservationService.createReservation(1L, 10L, in, out, 2);
        assertThat(created.getId()).isEqualTo(99L);
        assertThat(created.getState().getName()).isEqualTo("Confirmada");

        // Preparar reserva finalizada para reviews
        ReservationEntity finalized = ReservationEntity.builder()
                .id(created.getId())
                .user(user)
                .accommodation(accommodation)
                .state(stateFinalizada)
                .checkIn(in)
                .checkOut(out)
                .countRoommates(2)
                .build();
        when(reservationRepository.findById(99L)).thenReturn(Optional.of(finalized));

        // Stubs rating
        when(guestRatingRepository.findByUser_IdAndReservation_Id(1L, 99L)).thenReturn(Optional.empty());
        when(guestRatingRepository.save(any(GuestRatingEntity.class)))
                .thenAnswer(inv -> {
                    GuestRatingEntity g = inv.getArgument(0, GuestRatingEntity.class);
                    // simular PK asignada por BD
                    try { var idField = GuestRatingEntity.class.getDeclaredField("id"); idField.setAccessible(true); idField.set(g, 500L);} catch (Exception ignored) {}
                    return g;
                });
        when(guestRatingRepository.findById(500L)).thenAnswer(inv ->
                Optional.of(GuestRatingEntity.builder()
                        .id(500L)
                        .user(user)
                        .reservation(finalized)
                        .rating(5)
                        .isCommentable(true)
                        .commentExpiration(LocalDate.now().plusDays(30))
                        .build())
        );

        // Act: calificar
        RatingCreateRequest rateReq = new RatingCreateRequest();
        rateReq.setReservationId(99L);
        rateReq.setRating(5);
        RatingResponse rateRes = reviewService.createRating(1L, rateReq);
        assertThat(rateRes.getId()).isEqualTo(500L);
        assertThat(rateRes.getRating()).isEqualTo(5);

        // Stubs comment save
        when(reviewCommentRepository.save(any(ReviewCommentEntity.class)))
                .thenAnswer(inv -> {
                    ReviewCommentEntity c = inv.getArgument(0, ReviewCommentEntity.class);
                    try { var idField = ReviewCommentEntity.class.getDeclaredField("id"); idField.setAccessible(true); idField.set(c, 700L);} catch (Exception ignored) {}
                    return c;
                });

        // Act: comentar
        CommentCreateRequest comReq = new CommentCreateRequest();
        comReq.setGuestRatingId(500L);
        comReq.setMessage("Excelente experiencia");
        CommentResponse comRes = reviewService.createComment(1L, comReq);
        assertThat(comRes.getId()).isEqualTo(700L);
        assertEquals("Excelente experiencia", comRes.getMessage());

        // Verificar duplicidad: segundo rating debe fallar
        when(guestRatingRepository.findByUser_IdAndReservation_Id(1L, 99L))
                .thenReturn(Optional.of(GuestRatingEntity.builder().id(501L).user(user).reservation(finalized).rating(4).build()));
        assertThrows(IllegalStateException.class, () -> reviewService.createRating(1L, rateReq));

        // Verify guardados
        verify(reservationRepository, atLeastOnce()).save(any(ReservationEntity.class));
        verify(guestRatingRepository).save(any(GuestRatingEntity.class));
        verify(reviewCommentRepository).save(any(ReviewCommentEntity.class));
    }
}
