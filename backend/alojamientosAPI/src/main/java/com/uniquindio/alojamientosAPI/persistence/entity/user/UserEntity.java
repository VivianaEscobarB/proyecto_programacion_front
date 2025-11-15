package com.uniquindio.alojamientosAPI.persistence.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id_int")
    private Long id;

    @Column(name = "firstname", nullable = false, length = 50)
    private String firstName;

    @Column(name = "lastname", nullable = false, length = 50)
    private String lastName;

    @Column(name = "dayofbirth")
    private LocalDate dayOfBirth;

    @Column(name = "phonenumber", length = 50)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "url_accountphoto", columnDefinition = "TEXT")
    private String urlAccountPhoto;

    @Column(name = "homeaddress", columnDefinition = "TEXT")
    private String homeAddress;

    // Relación con roles (sin cascadas peligrosas)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id_int", foreignKey = @ForeignKey(name = "fk_user_roles_user")),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id_int", foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    )
    private Set<RoleEntity> roles;
}
