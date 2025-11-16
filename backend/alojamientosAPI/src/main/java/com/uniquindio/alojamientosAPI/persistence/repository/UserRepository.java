package com.uniquindio.alojamientosAPI.persistence.repository;

import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = "roles") // 🔹 carga roles junto al usuario
    Optional<UserEntity> findByEmail(String email);
}
