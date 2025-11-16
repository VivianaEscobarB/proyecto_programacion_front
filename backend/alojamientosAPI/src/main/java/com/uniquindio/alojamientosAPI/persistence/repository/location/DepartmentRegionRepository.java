package com.uniquindio.alojamientosAPI.persistence.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uniquindio.alojamientosAPI.persistence.entity.location.DepartmentRegionEntity;

@Repository
public interface DepartmentRegionRepository extends JpaRepository<DepartmentRegionEntity, Long> {
}

