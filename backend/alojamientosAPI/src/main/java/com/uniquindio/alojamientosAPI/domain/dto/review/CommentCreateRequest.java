package com.uniquindio.alojamientosAPI.domain.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentCreateRequest {

    @NotNull
    private Long guestRatingId;

    private Long parentCommentId;

    @NotBlank
    private String message;

    public Long getGuestRatingId() { return guestRatingId; }
    public void setGuestRatingId(Long guestRatingId) { this.guestRatingId = guestRatingId; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

