package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.PictureEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.PictureRepository;
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
class PictureServiceTest {

    @Mock PictureRepository repository;
    @InjectMocks PictureService service;

    @Test
    void findByAccommodation_delegates() {
        when(repository.findByAccommodationId(10L))
                .thenReturn(List.of(PictureEntity.builder().id(5L).title("fachada").url("http://img").build()));
        assertThat(service.findByAccommodation(10L)).hasSize(1);
        verify(repository).findByAccommodationId(10L);
    }

    @Test
    void create_delegates() {
        when(repository.save(any(PictureEntity.class))).thenAnswer(inv -> inv.getArgument(0, PictureEntity.class));
        PictureEntity p = service.create(PictureEntity.builder().title("sala").url("http://img2").build());
        assertThat(p.getTitle()).isEqualTo("sala");
        verify(repository).save(any(PictureEntity.class));
    }

    @Test
    void delete_delegates() {
        service.delete(9L);
        verify(repository).deleteById(9L);
    }
}

