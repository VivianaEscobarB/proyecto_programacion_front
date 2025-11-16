package com.uniquindio.alojamientosAPI.domain.mapper.reservation;

import com.uniquindio.alojamientosAPI.domain.dto.reservation.ReservationResponse;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.location.CityEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;

import java.time.temporal.ChronoUnit;

public final class ReservationMapper {

    private ReservationMapper() {}

    public static ReservationResponse toResponse(ReservationEntity e) {
        if (e == null) return null;
        ReservationResponse r = new ReservationResponse();
        r.setId(e.getId());
        UserEntity u = e.getUser();
        AccommodationEntity a = e.getAccommodation();
        r.setUserId(u != null ? u.getId() : null);
        r.setAccommodationId(a != null ? a.getId() : null);
        r.setCheckIn(e.getCheckIn());
        r.setCheckOut(e.getCheckOut());
        r.setCountRoommates(e.getCountRoommates());
        r.setStateName(e.getState() != null ? e.getState().getName() : null);
        if (a != null) {
            r.setAccommodationTitle(a.getTitle());
            CityEntity city = a.getCity();
            r.setCityName(city != null ? city.getName() : null);
            Double price = a.getDayPrice();
            if (price != null && e.getCheckIn() != null && e.getCheckOut() != null) {
                long nights = Math.max(1, ChronoUnit.DAYS.between(e.getCheckIn(), e.getCheckOut()));
                r.setEstimatedTotal(price * nights);
            }
        }
        if (u != null) {
            String name = (u.getFirstName() != null ? u.getFirstName() : "").trim() + " " + (u.getLastName() != null ? u.getLastName() : "").trim();
            r.setGuestName(name.trim());
            r.setGuestEmail(u.getEmail());
        }
        return r;
    }
}
