package com.uniquindio.alojamientosAPI.domain.dto.reservation;

import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer countRoommates;
    private String stateName;

    // Campos enriquecidos para vistas de anfitrión
    private String accommodationTitle;
    private String cityName;
    private String guestName;
    private String guestEmail;
    private Double estimatedTotal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAccommodationId() { return accommodationId; }
    public void setAccommodationId(Long accommodationId) { this.accommodationId = accommodationId; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Integer getCountRoommates() { return countRoommates; }
    public void setCountRoommates(Integer countRoommates) { this.countRoommates = countRoommates; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getAccommodationTitle() { return accommodationTitle; }
    public void setAccommodationTitle(String accommodationTitle) { this.accommodationTitle = accommodationTitle; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public Double getEstimatedTotal() { return estimatedTotal; }
    public void setEstimatedTotal(Double estimatedTotal) { this.estimatedTotal = estimatedTotal; }
}
