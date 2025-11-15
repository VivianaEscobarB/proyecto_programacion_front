package com.uniquindio.alojamientosAPI.domain.service.review;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingCreateRequest;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;

public interface ReviewService {

    RatingResponse createRating(Long userId, RatingCreateRequest request);

    CommentResponse createComment(Long userId, CommentCreateRequest request);
}

