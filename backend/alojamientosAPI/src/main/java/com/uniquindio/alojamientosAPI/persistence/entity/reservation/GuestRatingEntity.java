package com.uniquindio.alojamientosAPI.persistence.entity.reservation;

import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "guest_rating")
public class GuestRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id_int")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reservation", nullable = false)
    private ReservationEntity reservation;

    @Column(name = "rating", nullable = false)
    private Integer rating; // entero 1..5 para alinear con BD (INTEGER)

    @Column(name = "iscommentable")
    private Boolean isCommentable;

    @Column(name = "commentexpiration")
    private LocalDate commentExpiration;
}
