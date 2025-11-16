package com.uniquindio.alojamientosAPI.web.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Instant expiresAt;
    private List<String> roles;
    private String email;
    private String urlAccountPhoto;
    private Long userId;
}

