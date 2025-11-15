package com.uniquindio.alojamientosAPI.config;

import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.*;
import com.uniquindio.alojamientosAPI.persistence.entity.location.*;
import com.uniquindio.alojamientosAPI.persistence.entity.reservation.StateReservationEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.*;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.AccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.accommodation.StateAccommodationRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.RoleRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.location.CountryRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.location.DepartmentRegionRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.location.CityRepository;
import com.uniquindio.alojamientosAPI.persistence.repository.reservation.StateReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               StateAccommodationRepository stateAccommodationRepository,
                               CountryRepository countryRepository,
                               DepartmentRegionRepository departmentRegionRepository,
                               CityRepository cityRepository,
                               AccommodationRepository accommodationRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (accommodationRepository.count() > 0) {
                return; // Ya hay datos
            }

            // Roles
            RoleEntity anfitrionRole = roleRepository.findByName(RoleEnum.ANFITRION).orElseGet(() ->
                    roleRepository.save(RoleEntity.builder().name(RoleEnum.ANFITRION).description("Rol anfitrión").build()));
            RoleEntity clienteRole = roleRepository.findByName(RoleEnum.CLIENTE).orElseGet(() ->
                    roleRepository.save(RoleEntity.builder().name(RoleEnum.CLIENTE).description("Rol cliente").build()));

            // Usuario anfitrión
            UserEntity host = UserEntity.builder()
                    .firstName("Carlos")
                    .lastName("Anfitrión")
                    .email("host@example.com")
                    .password(passwordEncoder.encode("Passw0rd!"))
                    .dayOfBirth(LocalDate.of(1990, 5, 15))
                    .roles(Set.of(anfitrionRole))
                    .build();
            host = userRepository.save(host);

            // Usuario cliente (Laura) para pruebas de favoritos
            UserEntity laura = UserEntity.builder()
                    .firstName("Laura")
                    .lastName("Cliente")
                    .email("laura@example.com")
                    .password(passwordEncoder.encode("1234"))
                    .dayOfBirth(LocalDate.of(1995, 3, 10))
                    .urlAccountPhoto("/assets/images/avatar/avatar.png")
                    .roles(Set.of(clienteRole, anfitrionRole))
                    .build();
            laura = userRepository.save(laura);

            // Estado alojamiento
            StateAccommodationEntity activo = stateAccommodationRepository.findByName("ACTIVO")
                    .orElseGet(() -> stateAccommodationRepository.save(StateAccommodationEntity.builder().name("ACTIVO").description("Disponible para reservas").build()));

            // País / Departamento / Ciudad
            CountryEntity colombia = countryRepository.save(CountryEntity.builder().name("Colombia").description("País de ejemplo").build());
            DepartmentRegionEntity quindio = departmentRegionRepository.save(DepartmentRegionEntity.builder().name("Quindío").country(colombia).description("Departamento Quindío").build());
            CityEntity filandia = cityRepository.save(CityEntity.builder().name("Filandia").departmentRegion(quindio).description("Ciudad turística Filandia").build());

            // Alojamiento 1
            AccommodationEntity casaCampo = AccommodationEntity.builder()
                    .title("Casa Campestre Filandia")
                    .description("Hermosa casa campestre con vista a las montañas")
                    .address("Vereda El Recreo")
                    .dayPrice(450000.0)
                    .capacity(6)
                    .hostUser(host)
                    .stateAccommodation(activo)
                    .city(filandia)
                    .pictures(List.of(
                            PictureEntity.builder().title("Principal").description("Fachada").isMain(true).url("/assets/images/deptos/casa_con_plantas.jpg").build(),
                            PictureEntity.builder().title("Sala").description("Sala interior").isMain(false).url("/assets/images/deptos/cocina_casa.jpg").build()
                    ))
                    .services(List.of(
                            ServiceEntity.builder().title("Wifi").description("Internet fibra óptica").categoryId(1L).build(),
                            ServiceEntity.builder().title("Piscina").description("Piscina climatizada").categoryId(2L).build()
                    ))
                    .build();

            // Persistir (cascade para pictures/services por orphanRemoval + @JoinColumn)
            accommodationRepository.save(casaCampo);

            // Alojamiento 2
            AccommodationEntity fincaRobles = AccommodationEntity.builder()
                    .title("Finca Los Robles")
                    .description("Finca ecológica rodeada de naturaleza")
                    .address("Km 3 vía Filandia")
                    .dayPrice(520000.0)
                    .capacity(10)
                    .hostUser(host)
                    .stateAccommodation(activo)
                    .city(filandia)
                    .pictures(List.of(
                            PictureEntity.builder().title("Principal").description("Entrada finca").isMain(true).url("/assets/images/deptos/finca_robles.png").build(),
                            PictureEntity.builder().title("Habitación").description("Habitación principal").isMain(false).url("/assets/images/deptos/hotel-1979406_1280.jpg").build()
                    ))
                    .services(List.of(
                            ServiceEntity.builder().title("Desayuno").description("Incluye desayuno típico").categoryId(3L).build(),
                            ServiceEntity.builder().title("Senderos").description("Senderos ecológicos").categoryId(4L).build()
                    ))
                    .build();

            accommodationRepository.save(fincaRobles);
        };
    }

    @Bean
    CommandLineRunner ensureReservationStates(StateReservationRepository repo) {
        return args -> {
            List<String> required = List.of("Pendiente","Confirmada","Aprobada","Rechazada","Cancelada","Finalizada");
            for (String name : required) {
                repo.findByName(name).orElseGet(() -> {
                    StateReservationEntity s = StateReservationEntity.builder().name(name).description("Estado inicial creado automáticamente").build();
                    return repo.save(s);
                });
            }
        };
    }
}
