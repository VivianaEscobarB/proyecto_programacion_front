package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.FavoriteEntity;
import java.util.List;
import java.util.Optional;

public interface FavoriteService {

    // ➕ Agregar un favorito
    FavoriteEntity addFavorite(FavoriteEntity favorite);

    // ❌ Eliminar un favorito por ID
    void delete(Long id);

    // ❌ Eliminar un favorito específico de un usuario (por userId + accommodationId)
    void removeFromUser(Long userId, Long accommodationId);

    // 📋 Listar todos los favoritos
    List<FavoriteEntity> listAll();

    // 🔍 Buscar favorito por ID
    Optional<FavoriteEntity> findById(Long id);

    // 🔍 Buscar todos los favoritos de un usuario
    List<FavoriteEntity> findByUser(Long userId);

    // 🔍 Buscar todos los usuarios que marcaron un alojamiento
    List<FavoriteEntity> findByAccommodation(Long accommodationId);
}
