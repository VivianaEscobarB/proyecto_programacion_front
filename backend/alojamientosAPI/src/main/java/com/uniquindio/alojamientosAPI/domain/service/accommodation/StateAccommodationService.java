package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.StateAccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.StateAccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateAccommodationService {

    private final StateAccommodationRepository repository;

    public List<StateAccommodationEntity> findAll() {
        return repository.findAll();
    }

    public StateAccommodationEntity create(StateAccommodationEntity entity) {
        return repository.save(entity);
    }
}
