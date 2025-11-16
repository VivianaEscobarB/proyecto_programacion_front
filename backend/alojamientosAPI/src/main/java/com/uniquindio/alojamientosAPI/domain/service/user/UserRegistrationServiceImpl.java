package com.uniquindio.alojamientosAPI.domain.service.user;

import com.uniquindio.alojamientosAPI.domain.dto.user.RegisterUserCommand;
import com.uniquindio.alojamientosAPI.domain.dto.user.RegisteredUserResult;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEnum;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.persistence.repository.RoleRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // Nueva política: mínimo 8 caracteres
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationServiceImpl(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisteredUserResult register(RegisterUserCommand command) {
        Objects.requireNonNull(command, "El comando de registro no puede ser nulo");
        validateFields(command);

        // Unicidad de email
        userRepository.findByEmail(command.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("El email ya está registrado");
        });

        // Roles (por defecto CLIENTE si no se envían)
        Set<RoleEntity> roles = resolveRoles(command.getRoles());

        // Construcción y guardado (incluye teléfono, fecha nacimiento y dirección)
        UserEntity user = UserEntity.builder()
                .firstName(trimOrNull(command.getFirstName()))
                .lastName(trimOrNull(command.getLastName()))
                .email(command.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(command.getPassword()))
                .phoneNumber(trimOrNull(command.getPhoneNumber()))
                .dayOfBirth(parseLocalDate(command.getDateOfBirth()))
                .homeAddress(trimOrNull(command.getAddress()))
                .roles(roles)
                .build();

        user = userRepository.save(user);

        List<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getName)
                .map(Enum::name)
                .collect(Collectors.toList());

        return RegisteredUserResult.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }

    private void validateFields(RegisterUserCommand cmd) {
        if (isBlank(cmd.getFirstName())) throw new IllegalArgumentException("El nombre es obligatorio");
        if (isBlank(cmd.getLastName())) throw new IllegalArgumentException("El apellido es obligatorio");
        if (isBlank(cmd.getEmail())) throw new IllegalArgumentException("El email es obligatorio");
        if (!EMAIL_PATTERN.matcher(cmd.getEmail().trim()).matches())
            throw new IllegalArgumentException("El formato de email es inválido");
        if (isBlank(cmd.getPassword())) throw new IllegalArgumentException("La contraseña es obligatoria");
        if (!PASSWORD_PATTERN.matcher(cmd.getPassword()).matches())
            throw new IllegalArgumentException("La contraseña debe tener mínimo 8 caracteres");
    }

    private Set<RoleEntity> resolveRoles(List<String> rolesRequested) {
        List<RoleEnum> targetEnums;
        if (rolesRequested == null || rolesRequested.isEmpty()) {
            targetEnums = List.of(RoleEnum.CLIENTE);
        } else {
            targetEnums = rolesRequested.stream()
                    .filter(Objects::nonNull)
                    .map(this::parseRole)
                    .distinct()
                    .toList();
            if (targetEnums.isEmpty()) targetEnums = List.of(RoleEnum.CLIENTE);
        }

        Set<RoleEntity> result = new HashSet<>();
        for (RoleEnum re : targetEnums) {
            RoleEntity role = roleRepository.findByName(re)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado en la base de datos: " + re.name()));
            result.add(role);
        }
        return result;
    }

    private RoleEnum parseRole(String value) {
        String n = normalize(value);
        switch (n) {
            case "administrador", "admin" -> {
                return RoleEnum.ADMINISTRADOR;
            }
            case "cliente", "user", "usuario" -> {
                return RoleEnum.CLIENTE;
            }
            case "anfitrion", "anfitrión", "host" -> {
                return RoleEnum.ANFITRION;
            }
            default -> throw new IllegalArgumentException("Rol inválido: " + value);
        }
    }

    private String normalize(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trimOrNull(String s) {
        return isBlank(s) ? null : s.trim();
    }

    private LocalDate parseLocalDate(String input) {
        try {
            if (input == null || input.isBlank()) return null;
            return LocalDate.parse(input.trim()); // espera formato ISO yyyy-MM-dd
        } catch (Exception ex) {
            return null;
        }
    }
}
