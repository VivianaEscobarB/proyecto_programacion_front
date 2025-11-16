package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.StateAccommodationEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.StateAccommodationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateAccommodationServiceTest {

    @Mock StateAccommodationRepository repository;
    @InjectMocks StateAccommodationService service;

    @Test
    void findAll_delegates() {
        when(repository.findAll()).thenReturn(List.of(StateAccommodationEntity.builder().id(1L).name("Activo").build()));
        assertThat(service.findAll()).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void create_delegates() {
        when(repository.save(any(StateAccommodationEntity.class))).thenAnswer(inv -> inv.getArgument(0, StateAccommodationEntity.class));
        StateAccommodationEntity s = service.create(StateAccommodationEntity.builder().name("Inactivo").build());
        assertThat(s.getName()).isEqualTo("Inactivo");
        verify(repository).save(any(StateAccommodationEntity.class));
    }
}
