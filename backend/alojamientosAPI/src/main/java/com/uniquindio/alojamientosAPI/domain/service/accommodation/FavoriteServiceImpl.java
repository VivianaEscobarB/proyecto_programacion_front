package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.FavoriteEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.FavoriteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public FavoriteEntity addFavorite(FavoriteEntity favorite) {
        if (favorite == null || favorite.getUser() == null || favorite.getUser().getId() == null
                || favorite.getAccommodation() == null || favorite.getAccommodation().getId() == null) {
            throw new IllegalArgumentException("Datos incompletos para agregar favorito");
        }

        Long userId = favorite.getUser().getId();
        Long accommodationId = favorite.getAccommodation().getId();

        boolean exists = favoriteRepository.existsByUser_IdAndAccommodation_Id(userId, accommodationId);
        if (exists) {
            throw new IllegalStateException("El alojamiento ya está en favoritos del usuario");
        }

        // Asegurar que se cree uno nuevo
        favorite.setId(null);
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null || !favoriteRepository.existsById(id)) {
            throw new EntityNotFoundException("Favorito no encontrado con id: " + id);
        }
        favoriteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removeFromUser(Long userId, Long accommodationId) {
        if (userId == null || accommodationId == null) {
            throw new IllegalArgumentException("Se requieren userId y accommodationId");
        }
        Optional<FavoriteEntity> fav = favoriteRepository.findByUser_IdAndAccommodation_Id(userId, accommodationId);
        if (fav.isEmpty()) {
            throw new EntityNotFoundException("No existe un favorito para el usuario y alojamiento dados");
        }
        favoriteRepository.deleteByUser_IdAndAccommodation_Id(userId, accommodationId);
    }

    @Override
    public List<FavoriteEntity> listAll() {
        return favoriteRepository.findAll();
    }

    @Override
    public Optional<FavoriteEntity> findById(Long id) {
        return favoriteRepository.findById(id);
    }

    @Override
    public List<FavoriteEntity> findByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId es requerido");
        }
        return favoriteRepository.findByUser_Id(userId);
    }

    @Override
    public List<FavoriteEntity> findByAccommodation(Long accommodationId) {
        if (accommodationId == null) {
            throw new IllegalArgumentException("accommodationId es requerido");
        }
        return favoriteRepository.findByAccommodation_Id(accommodationId);
    }
}

