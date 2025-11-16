package com.uniquindio.alojamientosAPI.persistence.entity.accommodation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id_int")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Solo IDs para mantener independencia funcional
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;
}
