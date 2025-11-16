package com.uniquindio.alojamientosAPI.web.review;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;
import com.uniquindio.alojamientosAPI.domain.service.review.ReviewService;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/client/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/ratings")
    public ResponseEntity<RatingResponse> createRating(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody RatingCreateRequest request
    ) {
        Long userId = extractUserId(principal);
        try {
            RatingResponse response = reviewService.createRating(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalStateException e) {
            // No autorizado, duplicado o no finalizada
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long userId = extractUserId(principal);
        try {
            CommentResponse response = reviewService.createComment(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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

