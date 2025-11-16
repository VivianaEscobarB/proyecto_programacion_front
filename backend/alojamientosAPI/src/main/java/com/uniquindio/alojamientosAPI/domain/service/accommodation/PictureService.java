package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.PictureEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final PictureRepository pictureRepository;

    public List<PictureEntity> findByAccommodation(Long accommodationId) {
        return pictureRepository.findByAccommodationId(accommodationId);
    }

    public PictureEntity create(PictureEntity entity) {
        return pictureRepository.save(entity);
    }

    public void delete(Long id) {
        pictureRepository.deleteById(id);
    }
}
