package com.uniquindio.alojamientosAPI.domain.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UserDetailDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dayOfBirth;
    private String phoneNumber;
    private String email;
    private String urlAccountPhoto;
    private String homeAddress;
    private List<String> roles;
}
