package com.uniquindio.alojamientosAPI.domain.service.review;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;
import com.uniquindio.alojamientosAPI.domain.mapper.review.ReviewMapper;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.GuestRatingEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReviewCommentEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.GuestRatingRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReservationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.ReviewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReservationRepository reservationRepository;
    private final GuestRatingRepository guestRatingRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepository userRepository;

    private static final String STATE_FINISHED = "Finalizada";

    @Override
    @Transactional
    public RatingResponse createRating(Long userId, RatingCreateRequest request) {
        if (userId == null || request == null || request.getReservationId() == null || request.getRating() == null) {
            throw new IllegalArgumentException("Datos incompletos para calificar");
        }

        ReservationEntity reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Solo el propietario puede calificar
        Long ownerId = reservation.getUser() != null ? reservation.getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new IllegalStateException("No autorizado para calificar esta reserva");
        }

        // Debe estar finalizada
        String state = reservation.getState() != null ? reservation.getState().getName() : null;
        if (!STATE_FINISHED.equalsIgnoreCase(state)) {
            throw new IllegalStateException("Solo se puede calificar una reserva finalizada");
        }

        // Evitar duplicidad de calificación para el mismo usuario y reserva
        guestRatingRepository.findByUser_IdAndReservation_Id(userId, reservation.getId())
                .ifPresent(gr -> { throw new IllegalStateException("Ya existe una calificación para esta reserva"); });

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        GuestRatingEntity entity = GuestRatingEntity.builder()
                .user(user)
                .reservation(reservation)
                .rating(request.getRating())
                .isCommentable(Boolean.TRUE)
                .commentExpiration(LocalDate.now().plusDays(30))
                .build();

        GuestRatingEntity saved = guestRatingRepository.save(entity);
        return ReviewMapper.toRatingResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, CommentCreateRequest request) {
        if (userId == null || request == null || request.getGuestRatingId() == null) {
            throw new IllegalArgumentException("Datos incompletos para comentar");
        }

        GuestRatingEntity rating = guestRatingRepository.findById(request.getGuestRatingId())
                .orElseThrow(() -> new IllegalArgumentException("Calificación no encontrada"));

        // Solo el propietario de la reserva puede comentar
        Long ownerId = rating.getReservation() != null && rating.getReservation().getUser() != null
                ? rating.getReservation().getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new IllegalStateException("No autorizado para comentar sobre esta reserva");
        }

        // Verificar que la reserva está finalizada
        String state = rating.getReservation() != null && rating.getReservation().getState() != null
                ? rating.getReservation().getState().getName() : null;
        if (!STATE_FINISHED.equalsIgnoreCase(state)) {
            throw new IllegalStateException("Solo se puede comentar una reserva finalizada");
        }

        // Verificar que se puede comentar y no ha expirado
        if (Boolean.FALSE.equals(rating.getIsCommentable())) {
            throw new IllegalStateException("La calificación no admite comentarios");
        }
        LocalDate exp = rating.getCommentExpiration();
        if (exp != null && LocalDate.now().isAfter(exp)) {
            throw new IllegalStateException("El periodo para comentar ha expirado");
        }

        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ReviewCommentEntity parent = null;
        if (request.getParentCommentId() != null) {
            parent = reviewCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Comentario padre no encontrado"));
        }

        ReviewCommentEntity comment = ReviewCommentEntity.builder()
                .guestRating(rating)
                .user(author)
                .message(request.getMessage())
                .parentComment(parent)
                .build();

        ReviewCommentEntity saved = reviewCommentRepository.save(comment);
        return ReviewMapper.toCommentResponse(saved);
    }
}
