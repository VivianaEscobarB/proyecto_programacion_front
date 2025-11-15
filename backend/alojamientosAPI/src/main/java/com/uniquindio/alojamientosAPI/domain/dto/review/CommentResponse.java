package com.uniquindio.alojamientosAPI.domain.dto.review;

public class CommentResponse {
    private Long id;
    private Long guestRatingId;
    private Long userId;
    private String message;
    private Long parentCommentId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGuestRatingId() { return guestRatingId; }
    public void setGuestRatingId(Long guestRatingId) { this.guestRatingId = guestRatingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }
}

