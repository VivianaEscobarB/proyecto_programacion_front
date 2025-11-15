package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.ServiceEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceEntity> findByAccommodation(Long accommodationId) {
        return serviceRepository.findByAccommodationId(accommodationId);
    }

    public ServiceEntity create(ServiceEntity entity) {
        return serviceRepository.save(entity);
    }

    public void delete(Long id) {
        serviceRepository.deleteById(id);
    }
}
