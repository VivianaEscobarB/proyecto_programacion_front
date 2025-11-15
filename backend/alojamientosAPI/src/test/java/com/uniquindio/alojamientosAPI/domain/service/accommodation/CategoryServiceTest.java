package com.uniquindio.alojamientosAPI.domain.service.accommodation;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.CategoryEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.CategoryRepository;
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
class CategoryServiceTest {

    @Mock CategoryRepository repository;
    @InjectMocks CategoryService service;

    @Test
    void findAll_delegatesToRepository() {
        when(repository.findAll()).thenReturn(List.of(CategoryEntity.builder().id(1L).name("Casa").build()));
        assertThat(service.findAll()).hasSize(1).first().extracting("name").isEqualTo("Casa");
        verify(repository).findAll();
    }

    @Test
    void create_delegatesToRepository() {
        CategoryEntity c = CategoryEntity.builder().name("Apartamento").build();
        when(repository.save(any(CategoryEntity.class))).thenAnswer(inv -> inv.getArgument(0, CategoryEntity.class));
        CategoryEntity saved = service.create(c);
        assertThat(saved.getName()).isEqualTo("Apartamento");
        verify(repository).save(any(CategoryEntity.class));
    }
}

