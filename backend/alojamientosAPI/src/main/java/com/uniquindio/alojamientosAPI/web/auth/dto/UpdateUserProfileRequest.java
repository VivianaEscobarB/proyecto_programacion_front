package com.uniquindio.alojamientosAPI.web.auth.dto;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String phoneNumber;
    private String homeAddress;
    // esperado en formato ISO yyyy-MM-dd
    private String dayOfBirth;
}

