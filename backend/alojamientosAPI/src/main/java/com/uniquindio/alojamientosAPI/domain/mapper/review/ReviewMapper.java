package com.uniquindio.alojamientosAPI.domain.mapper.review;

import com.uniquindio.alojamientosAPI.domain.dto.review.CommentResponse;
import com.uniquindio.alojamientosAPI.domain.dto.review.RatingResponse;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.GuestRatingEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReviewCommentEntity;

public final class ReviewMapper {

    private ReviewMapper() {}

    public static RatingResponse toRatingResponse(GuestRatingEntity e) {
        if (e == null) return null;
        RatingResponse r = new RatingResponse();
        r.setId(e.getId());
        r.setReservationId(e.getReservation() != null ? e.getReservation().getId() : null);
        r.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        r.setRating(e.getRating());
        r.setCommentable(e.getIsCommentable());
        r.setCommentExpiration(e.getCommentExpiration());
        return r;
    }

    public static CommentResponse toCommentResponse(ReviewCommentEntity e) {
        if (e == null) return null;
        CommentResponse c = new CommentResponse();
        c.setId(e.getId());
        c.setGuestRatingId(e.getGuestRating() != null ? e.getGuestRating().getId() : null);
        c.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        c.setMessage(e.getMessage());
        c.setParentCommentId(e.getParentComment() != null ? e.getParentComment().getId() : null);
        return c;
    }
}

