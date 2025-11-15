package com.uniquindio.alojamientosAPI.persistence.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uniquindio.alojamientosAPI.persistence.entity.location.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
}

