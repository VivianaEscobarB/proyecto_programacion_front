package com.uniquindio.alojamientosAPI.domain.dto.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ReservationCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @Future
    private LocalDate checkOut;

    @NotNull
    @Positive
    private Integer countRoommates;

    public Long getAccommodationId() { return accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Integer getCountRoommates() { return countRoommates; }
    public void setCountRoommates(Integer countRoommates) { this.countRoommates = countRoommates; }
}

