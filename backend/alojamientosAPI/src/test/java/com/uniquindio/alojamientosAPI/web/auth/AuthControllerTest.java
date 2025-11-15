package com.uniquindio.alojamientosAPI.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.alojamientosAPI.config.SecurityConfig;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthEntryPoint;
import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthenticationFilter;
import com.uniquindio.alojamientosAPI.security.jwt.JwtService;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEnum;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthEntryPoint.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    JwtService jwtService;

    // Necesario por SecurityConfig
    @MockBean
    com.uniquindio.alojamientosAPI.security.auth.CustomUserDetailsService customUserDetailsService;

    // Necesario por AuthController
    @MockBean
    com.uniquindio.alojamientosAPI.domain.service.user.UserRegistrationService userRegistrationService;

    private String strongPwd() {
        return "Aa1!" + UUID.randomUUID();
    }

    @Test
    @DisplayName("Login exitoso devuelve token y datos básicos")
    void loginSuccess() throws Exception {
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .password("encoded-dummy")
                .roles(Set.of(RoleEntity.builder().name(RoleEnum.CLIENTE).build()))
                .build();
        CustomUserDetails cud = new CustomUserDetails(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(cud, null, cud.getAuthorities());
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(auth);
        Mockito.when(jwtService.generateToken(any())).thenReturn("fake.jwt.token");
        Mockito.when(jwtService.extractExpiration(eq("fake.jwt.token"))).thenReturn(java.util.Date.from(Instant.now().plusSeconds(3600)));

        String body = objectMapper.writeValueAsString(
                Map.of(
                        "email", "user@example.com",
                        "password", strongPwd()
                )
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("/api/auth/me requiere autenticación")
    void meRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
