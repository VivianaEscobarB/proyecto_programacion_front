package com.uniquindio.alojamientosAPI.web;

import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthEntryPoint;
import com.uniquindio.alojamientosAPI.security.jwt.JwtAuthenticationFilter;
import com.uniquindio.alojamientosAPI.security.jwt.JwtService;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetailsService;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEnum;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
@Import({TestControllerAuthzTest.MockBeansConfig.class, TestControllerAuthzTest.SecurityOverrideConfig.class})
class TestControllerAuthzTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @TestConfiguration
    static class MockBeansConfig {
        @Bean
        JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }
    }

    @TestConfiguration
    static class SecurityOverrideConfig {
        @Bean
        JwtAuthEntryPoint jwtAuthEntryPoint() {
            return new JwtAuthEntryPoint();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
            return new JwtAuthenticationFilter(jwtService, customUserDetailsService);
        }

        @Bean
        SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
                                        JwtAuthenticationFilter jwtAuthenticationFilter,
                                        JwtAuthEntryPoint jwtAuthEntryPoint) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(new AntPathRequestMatcher("/api/public/**")).permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                    .requestMatchers("/api/host/**").hasRole("ANFITRION")
                    .requestMatchers("/api/client/**").hasRole("CLIENTE")
                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    @Test
    @DisplayName("/api/public/hello es público (200)")
    void publicHello_isPublic() throws Exception {
        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PÚBLICO")));
    }

    @Test
    @DisplayName("/api/private/hello sin token devuelve 401")
    void privateHello_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/private/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/api/private/hello permite CLIENTE (200)")
    void privateHello_allowsClient() throws Exception {
        String token = "tkn-client";
        String email = "client@example.com";
        CustomUserDetails cud = buildUser(email, RoleEnum.CLIENTE);
        Mockito.when(jwtService.extractUsername(token)).thenReturn(email);
        Mockito.when(customUserDetailsService.loadUserByUsername(email)).thenReturn(cud);
        Mockito.when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);

        mockMvc.perform(get("/api/private/hello").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PRIVADO")));
    }

    @Test
    @DisplayName("/api/admin/hello con CLIENTE devuelve 403")
    void adminHello_deniesClient() throws Exception {
        String token = "tkn-client";
        String email = "client@example.com";
        CustomUserDetails cud = buildUser(email, RoleEnum.CLIENTE);
        Mockito.when(jwtService.extractUsername(token)).thenReturn(email);
        Mockito.when(customUserDetailsService.loadUserByUsername(email)).thenReturn(cud);
        Mockito.when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);

        mockMvc.perform(get("/api/admin/hello").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/api/admin/hello permite ADMINISTRADOR (200)")
    void adminHello_allowsAdmin() throws Exception {
        String token = "tkn-admin";
        String email = "admin@example.com";
        CustomUserDetails cud = buildUser(email, RoleEnum.ADMINISTRADOR);
        Mockito.when(jwtService.extractUsername(token)).thenReturn(email);
        Mockito.when(customUserDetailsService.loadUserByUsername(email)).thenReturn(cud);
        Mockito.when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);

        mockMvc.perform(get("/api/admin/hello").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ADMINISTRADOR")));
    }

    private CustomUserDetails buildUser(String email, RoleEnum role) {
        UserEntity ue = UserEntity.builder()
                .email(email)
                .password("encoded-dummy")
                .roles(Set.of(RoleEntity.builder().name(role).build()))
                .build();
        return new CustomUserDetails(ue);
    }
}
