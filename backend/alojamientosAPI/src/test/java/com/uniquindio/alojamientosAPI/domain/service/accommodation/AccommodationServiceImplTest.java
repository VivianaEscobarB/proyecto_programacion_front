package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.StateAccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEnum;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.AccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.StateAccommodationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {

    @Mock AccommodationRepository accommodationRepository;
    @Mock UserRepository userRepository;
    @Mock StateAccommodationRepository stateAccommodationRepository;

    @InjectMocks AccommodationServiceImpl service;

    private UserEntity hostDb;

    @BeforeEach
    void setUp() {
        hostDb = UserEntity.builder()
                .id(10L)
                .roles(Set.of(RoleEntity.builder().name(RoleEnum.ANFITRION).build()))
                .build();
    }

    @Test
    @DisplayName("create() persiste cuando el usuario tiene rol ANFITRION")
    void create_withHostRole_persists() {
        AccommodationEntity entity = new AccommodationEntity();
        entity.setHostUser(UserEntity.builder().id(10L).build());

        when(userRepository.findById(10L)).thenReturn(Optional.of(hostDb));
        when(accommodationRepository.save(any(AccommodationEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0, AccommodationEntity.class));

        AccommodationEntity saved = service.create(entity);

        assertNotNull(saved);
        verify(accommodationRepository).save(any(AccommodationEntity.class));
    }

    @Test
    @DisplayName("create() lanza error cuando el usuario no es ANFITRION")
    void create_withoutHostRole_throws() {
        AccommodationEntity entity = new AccommodationEntity();
        entity.setHostUser(UserEntity.builder().id(11L).build());

        UserEntity notHost = UserEntity.builder()
                .id(11L)
                .roles(Set.of(RoleEntity.builder().name(RoleEnum.CLIENTE).build()))
                .build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(notHost));

        assertThrows(IllegalStateException.class, () -> service.create(entity));
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() lanza EntityNotFound si no existe el alojamiento")
    void update_notExists_throws() {
        when(accommodationRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> service.update(1L, new AccommodationEntity()));
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() lanza EntityNotFound si no existe el alojamiento")
    void delete_notExists_throws() {
        when(accommodationRepository.existsById(2L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.delete(2L));
        verify(accommodationRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("updateState() cambia el estado y persiste")
    void updateState_success() {
        AccommodationEntity acc = new AccommodationEntity();
        acc.setId(5L);
        when(accommodationRepository.findById(5L)).thenReturn(Optional.of(acc));
        when(stateAccommodationRepository.findByName("Activo"))
                .thenReturn(Optional.of(StateAccommodationEntity.builder().id(7L).name("Activo").build()));
        when(accommodationRepository.save(any(AccommodationEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0, AccommodationEntity.class));

        AccommodationEntity updated = service.updateState(5L, "Activo");

        assertNotNull(updated.getStateAccommodation());
        assertEquals("Activo", updated.getStateAccommodation().getName());
        verify(accommodationRepository).save(any(AccommodationEntity.class));
    }

    @Test
    @DisplayName("activate() delega en updateState('Activo')")
    void activate_delegatesToUpdateState() {
        AccommodationEntity acc = new AccommodationEntity();
        acc.setId(9L);
        when(accommodationRepository.findById(9L)).thenReturn(Optional.of(acc));
        when(stateAccommodationRepository.findByName("Activo"))
                .thenReturn(Optional.of(StateAccommodationEntity.builder().id(8L).name("Activo").build()));
        when(accommodationRepository.save(any(AccommodationEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0, AccommodationEntity.class));

        AccommodationEntity res = service.activate(9L);
        assertEquals("Activo", res.getStateAccommodation().getName());
        verify(stateAccommodationRepository).findByName("Activo");
    }

    @Test
    @DisplayName("updateState() con nombre en blanco lanza IllegalArgumentException")
    void updateState_blankName_throws() {
        when(accommodationRepository.findById(3L)).thenReturn(Optional.of(new AccommodationEntity()));
        assertThrows(IllegalArgumentException.class, () -> service.updateState(3L, " "));
    }
}
