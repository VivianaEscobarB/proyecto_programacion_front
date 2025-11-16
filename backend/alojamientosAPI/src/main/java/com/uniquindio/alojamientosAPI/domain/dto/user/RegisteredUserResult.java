package com.uniquindio.alojamientosAPI.domain.dto.user;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RegisteredUserResult {
    Long id;
    String email;
    List<String> roles;
}

