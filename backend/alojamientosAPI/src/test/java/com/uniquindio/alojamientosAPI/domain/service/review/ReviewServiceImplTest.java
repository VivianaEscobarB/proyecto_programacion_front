package com.uniquindio.alojamientosAPI.domain.service.review;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.GuestRatingEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReviewCommentEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.GuestRatingRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReviewCommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock ReservationRepository reservationRepository;
    @Mock GuestRatingRepository guestRatingRepository;
    @Mock ReviewCommentRepository reviewCommentRepository;
    @Mock UserRepository userRepository;

    @InjectMocks ReviewServiceImpl service;

    @Test
    void createRating_happyPath() {
        Long userId = 77L;
        Long reservationId = 999L;

        RatingCreateRequest req = new RatingCreateRequest();
        req.setReservationId(reservationId);
        req.setRating(5);

        // Reserva finalizada cuyo propietario es userId
        ReservationEntity reservation = ReservationEntity.builder()
                .id(reservationId)
                .user(UserEntity.builder().id(userId).build())
                .state(StateReservationEntity.builder().name("Finalizada").build())
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(guestRatingRepository.findByUser_IdAndReservation_Id(userId, reservationId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build()));
        when(guestRatingRepository.save(any(GuestRatingEntity.class))).thenAnswer(inv -> {
            GuestRatingEntity e = inv.getArgument(0, GuestRatingEntity.class);
            e.setId(321L);
            return e;
        });

        RatingResponse res = service.createRating(userId, req);

        assertThat(res.getId()).isEqualTo(321L);
        assertThat(res.getReservationId()).isEqualTo(reservationId);
        assertThat(res.getUserId()).isEqualTo(userId);
        assertThat(res.getRating()).isEqualTo(5);

        ArgumentCaptor<GuestRatingEntity> captor = ArgumentCaptor.forClass(GuestRatingEntity.class);
        verify(guestRatingRepository).save(captor.capture());
        assertThat(captor.getValue().getIsCommentable()).isTrue();
        assertThat(captor.getValue().getCommentExpiration()).isAfterOrEqualTo(LocalDate.now());
    }

    @Test
    void createComment_happyPath_withParent() {
        Long userId = 55L;
        Long guestRatingId = 88L;
        Long parentId = 11L;

        CommentCreateRequest req = new CommentCreateRequest();
        req.setGuestRatingId(guestRatingId);
        req.setParentCommentId(parentId);
        req.setMessage("Muy buena experiencia");

        // guest rating asociado a reserva finalizada y del mismo owner
        ReservationEntity reservation = ReservationEntity.builder()
                .id(900L)
                .user(UserEntity.builder().id(userId).build())
                .state(StateReservationEntity.builder().name("Finalizada").build())
                .build();

        GuestRatingEntity rating = GuestRatingEntity.builder()
                .id(guestRatingId)
                .reservation(reservation)
                .isCommentable(Boolean.TRUE)
                .commentExpiration(LocalDate.now().plusDays(10))
                .build();

        when(guestRatingRepository.findById(guestRatingId)).thenReturn(Optional.of(rating));
        when(userRepository.findById(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build()));
        when(reviewCommentRepository.findById(parentId)).thenReturn(Optional.of(ReviewCommentEntity.builder().id(parentId).build()));
        when(reviewCommentRepository.save(any(ReviewCommentEntity.class))).thenAnswer(inv -> {
            ReviewCommentEntity c = inv.getArgument(0, ReviewCommentEntity.class);
            c.setId(777L);
            return c;
        });

        CommentResponse res = service.createComment(userId, req);

        assertThat(res.getId()).isEqualTo(777L);
        assertThat(res.getGuestRatingId()).isEqualTo(guestRatingId);
        assertThat(res.getUserId()).isEqualTo(userId);
        assertThat(res.getParentCommentId()).isEqualTo(parentId);
        assertThat(res.getMessage()).isEqualTo("Muy buena experiencia");

        verify(reviewCommentRepository).save(any(ReviewCommentEntity.class));
    }
}

