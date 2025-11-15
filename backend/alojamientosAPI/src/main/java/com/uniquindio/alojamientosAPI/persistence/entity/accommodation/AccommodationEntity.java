package com.uniquindio.alojamientosAPI.persistence.entity.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.location.CityEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.ReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id_int")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String address;

    @Column(name = "dayprice")
    private Double dayPrice;

    private Integer capacity;

    // ============================================================
    // 🔗 Relaciones
    // ============================================================

    // 🧍 Anfitrión del alojamiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", nullable = false)
    private UserEntity hostUser;

    // 🏷️ Estado del alojamiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_accommodation_id", nullable = false)
    private StateAccommodationEntity stateAccommodation;

    // 🏙️ Ciudad donde se encuentra el alojamiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accommodation_id") // 🔗 conecta con el campo de la tabla secundaria
    private List<PictureEntity> pictures;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "accommodation_id")
    private List<ServiceEntity> services;

    // ⭐ Favoritos (relación inversa)
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteEntity> favorites;

    // 🏠 Reservas asociadas
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations;
}
