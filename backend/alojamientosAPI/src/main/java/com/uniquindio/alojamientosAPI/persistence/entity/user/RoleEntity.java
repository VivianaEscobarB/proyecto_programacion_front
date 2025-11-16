package com.uniquindio.alojamientosAPI.persistence.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_roles_name", columnNames = {"name"})
       })
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER) // PostgreSQL usa int4 para SERIAL
    @Column(name = "id_int")
    private Integer id;

    @Convert(converter = RoleEnumConverter.class)
    @Column(name = "name", nullable = false, length = 50)
    private RoleEnum name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
