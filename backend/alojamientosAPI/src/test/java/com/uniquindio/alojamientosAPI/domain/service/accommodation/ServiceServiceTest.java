package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.ServiceEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.ServiceRepository;
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
class ServiceServiceTest {

    @Mock ServiceRepository repository;
    @InjectMocks ServiceService service;

    @Test
    void findByAccommodation_delegates() {
        when(repository.findByAccommodationId(1L)).thenReturn(List.of(ServiceEntity.builder().id(3L).title("wifi").build()));
        assertThat(service.findByAccommodation(1L)).hasSize(1);
        verify(repository).findByAccommodationId(1L);
    }

    @Test
    void create_delegates() {
        when(repository.save(any(ServiceEntity.class))).thenAnswer(inv -> inv.getArgument(0, ServiceEntity.class));
        ServiceEntity s = service.create(ServiceEntity.builder().title("wifi").build());
        assertThat(s.getTitle()).isEqualTo("wifi");
        verify(repository).save(any(ServiceEntity.class));
    }

    @Test
    void delete_delegates() {
        service.delete(7L);
        verify(repository).deleteById(7L);
    }
}
