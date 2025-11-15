package com.uniquindio.alojamientosAPI.web.accommodation;

import com.uniquindio.alojamientosAPI.config.SecurityConfig;
import com.uniquindio.alojamientosAPI.domain.dto.accommodation.AccommodationDTO;
import com.uniquindio.alojamientosAPI.domain.mapper.accommodation.AccommodationMapper;
import com.uniquindio.alojamientosAPI.domain.service.accommodation.AccommodationService;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.AccommodationEntity;
import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthEntryPoint;
import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthenticationFilter;
import com.uniquindio.alojamientosAPI.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccommodationController.class)
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
@Import({SecurityConfig.class, AccommodationControllerSecurityTest.MockBeansConfig.class, AccommodationControllerSecurityTest.PassThroughJwtFilterConfig.class})
class AccommodationControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccommodationService accommodationService;

    @Autowired
    AccommodationMapper accommodationMapper;

    @TestConfiguration
    static class MockBeansConfig {
        @Bean
        AccommodationService accommodationService() { return Mockito.mock(AccommodationService.class); }
        @Bean
        AccommodationMapper accommodationMapper() { return Mockito.mock(AccommodationMapper.class); }
        @Bean
        JwtService jwtService() { return Mockito.mock(JwtService.class); }
        @Bean
        com.uniquindio.alojamientosAPI.security.auth.CustomUserDetailsService customUserDetailsService() { return Mockito.mock(com.uniquindio.alojamientosAPI.security.auth.CustomUserDetailsService.class); }
        @Bean
        JwtAuthEntryPoint jwtAuthEntryPoint() { return new JwtAuthEntryPoint(); }
    }

    @TestConfiguration
    static class PassThroughJwtFilterConfig {
        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                        com.uniquindio.alojamientosAPI.security.auth.CustomUserDetailsService customUserDetailsService) {
            return new JwtAuthenticationFilter(jwtService, customUserDetailsService) {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                    // No validar token en estos tests, solo dejar pasar para que se aplique autorización
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    @Test
    @DisplayName("POST /api/accommodations con CLIENTE -> 403")
    @WithMockUser(roles = {"CLIENTE"})
    void create_withCliente_forbidden() throws Exception {
        mockMvc.perform(post("/api/accommodations")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/accommodations con ANFITRION -> 200")
    @WithMockUser(roles = {"ANFITRION"})
    void create_withHost_ok() throws Exception {
        AccommodationEntity entity = new AccommodationEntity();
        AccommodationDTO dto = AccommodationDTO.builder().id(1).title("T").build();
        Mockito.when(accommodationMapper.toEntity(any())).thenReturn(entity);
        Mockito.when(accommodationService.create(any())).thenReturn(entity);
        Mockito.when(accommodationMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/accommodations")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"T\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/accommodations/{id} con CLIENTE -> 403")
    @WithMockUser(roles = {"CLIENTE"})
    void update_withCliente_forbidden() throws Exception {
        mockMvc.perform(put("/api/accommodations/1")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/accommodations/{id} con CLIENTE -> 403")
    @WithMockUser(roles = {"CLIENTE"})
    void delete_withCliente_forbidden() throws Exception {
        mockMvc.perform(delete("/api/accommodations/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/accommodations/{id}/activate con ADMINISTRADOR -> 200")
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void activate_withAdmin_ok() throws Exception {
        AccommodationEntity entity = new AccommodationEntity();
        AccommodationDTO dto = AccommodationDTO.builder().id(1).title("T").build();
        Mockito.when(accommodationService.activate(1L)).thenReturn(entity);
        Mockito.when(accommodationMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(post("/api/accommodations/1/activate"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/accommodations/{id}/activate con CLIENTE -> 403")
    @WithMockUser(roles = {"CLIENTE"})
    void activate_withCliente_forbidden() throws Exception {
        mockMvc.perform(post("/api/accommodations/1/activate"))
                .andExpect(status().isForbidden());
    }
}
