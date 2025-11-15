package com.uniquindio.alojamientosAPI.domain.service.user;

import com.uniquindio.alojamientosAPI.domain.dto.user.RegisterUserCommand;
import com.uniquindio.alojamientosAPI.domain.dto.user.RegisteredUserResult;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEnum;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.RoleRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserRegistrationServiceImpl service;

    private RegisterUserCommand baseCommand() {
        return RegisterUserCommand.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                // Genera una contraseña fuerte dinámica para evitar hardcode de secretos en el repo
                .password(generateStrongTestPassword())
                .phoneNumber("3001234567")
                .build();
    }

    private String generateStrongTestPassword() {
        // Cumple: minúscula, mayúscula, dígito y símbolo
        return "Aa1!" + UUID.randomUUID();
    }

    @Test
    @DisplayName("register: éxito con rol por defecto CLIENTE")
    void register_success_defaultRole() {
        RegisterUserCommand cmd = baseCommand();

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleEnum.CLIENTE)).thenReturn(Optional.of(RoleEntity.builder().name(RoleEnum.CLIENTE).build()));
        when(passwordEncoder.encode(any())).thenReturn("$2a$encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0, UserEntity.class);
            u.setId(123L);
            return u;
        });

        RegisteredUserResult res = service.register(cmd);

        assertEquals(123L, res.getId());
        assertEquals("juan@example.com", res.getEmail());
        assertTrue(res.getRoles().contains("CLIENTE"));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertEquals("$2a$encoded", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("register: email ya existe lanza IllegalArgumentException")
    void register_emailExists_throws() {
        RegisterUserCommand cmd = baseCommand();
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(UserEntity.builder().id(1L).build()));
        assertThrows(IllegalArgumentException.class, () -> service.register(cmd));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register: email inválido o password débil lanza IllegalArgumentException")
    void register_invalidEmailOrPassword_throws() {
        RegisterUserCommand badEmail = baseCommand();
        badEmail.setEmail("sin-arroba");
        assertThrows(IllegalArgumentException.class, () -> service.register(badEmail));

        RegisterUserCommand weakPwd = baseCommand();
        weakPwd.setPassword("weak");
        assertThrows(IllegalArgumentException.class, () -> service.register(weakPwd));
    }

    @Test
    @DisplayName("register: mapea roles normalizados (admin, cliente)")
    void register_rolesNormalized_success() {
        RegisterUserCommand cmd = baseCommand();
        cmd.setRoles(List.of("admin", "cliente"));

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleEnum.ADMINISTRADOR)).thenReturn(Optional.of(RoleEntity.builder().name(RoleEnum.ADMINISTRADOR).build()));
        when(roleRepository.findByName(RoleEnum.CLIENTE)).thenReturn(Optional.of(RoleEntity.builder().name(RoleEnum.CLIENTE).build()));
        when(passwordEncoder.encode(any())).thenReturn("$2a$enc");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0, UserEntity.class);
            u.setId(456L);
            return u;
        });

        RegisteredUserResult res = service.register(cmd);

        assertEquals(456L, res.getId());
        assertEquals(Set.of("ADMINISTRADOR", "CLIENTE"), Set.copyOf(res.getRoles()));
    }
}
