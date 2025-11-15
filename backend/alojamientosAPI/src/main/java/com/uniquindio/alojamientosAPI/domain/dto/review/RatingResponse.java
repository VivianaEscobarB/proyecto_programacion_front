package com.uniquindio.alojamientosAPI.domain.dto.review;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class RatingResponse {
    private Long id;
    private Long reservationId;
    private Long userId;
    private Integer rating;
    private Boolean commentable;
    private LocalDate commentExpiration;

}
