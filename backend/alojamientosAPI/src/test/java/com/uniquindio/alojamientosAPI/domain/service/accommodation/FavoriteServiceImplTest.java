package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.FavoriteEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.FavoriteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock FavoriteRepository favoriteRepository;
    @InjectMocks FavoriteServiceImpl service;

    private FavoriteEntity buildFav(Long userId, Long accId) {
        return FavoriteEntity.builder()
                .user(UserEntity.builder().id(userId).build())
                .accommodation(AccommodationEntity.builder().id(accId).build())
                .build();
    }

    @Test
    @DisplayName("addFavorite crea cuando no existe uno igual")
    void addFavorite_success() {
        FavoriteEntity fav = buildFav(1L, 10L);
        when(favoriteRepository.existsByUser_IdAndAccommodation_Id(1L, 10L)).thenReturn(false);
        when(favoriteRepository.save(any(FavoriteEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0, FavoriteEntity.class));

        FavoriteEntity saved = service.addFavorite(fav);

        assertNotNull(saved);
        verify(favoriteRepository).save(any(FavoriteEntity.class));
    }

    @Test
    @DisplayName("addFavorite lanza IllegalState cuando ya existe")
    void addFavorite_duplicate_throws() {
        FavoriteEntity fav = buildFav(1L, 10L);
        when(favoriteRepository.existsByUser_IdAndAccommodation_Id(1L, 10L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.addFavorite(fav));
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete lanza EntityNotFound cuando no existe por id")
    void delete_notExists_throws() {
        when(favoriteRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.delete(99L));
        verify(favoriteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("removeFromUser lanza EntityNotFound si no existe combinación")
    void removeFromUser_notFound_throws() {
        when(favoriteRepository.findByUser_IdAndAccommodation_Id(1L, 10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.removeFromUser(1L, 10L));
        verify(favoriteRepository, never()).deleteByUser_IdAndAccommodation_Id(any(), any());
    }

    @Test
    @DisplayName("findByUser con userId nulo lanza IllegalArgumentException")
    void findByUser_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.findByUser(null));
    }

    @Test
    @DisplayName("findByAccommodation con accommodationId nulo lanza IllegalArgumentException")
    void findByAccommodation_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.findByAccommodation(null));
    }
}

