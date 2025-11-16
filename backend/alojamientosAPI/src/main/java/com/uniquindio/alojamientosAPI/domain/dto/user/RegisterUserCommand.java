package com.uniquindio.alojamientosAPI.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserCommand {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private String phoneNumber;
    private String identification;
    private String dateOfBirth;
    private String nationality;
    private String country;
    private String department;
    private String city;
    private String address;
    private List<String> roles; // opcional; si es null/empty => CLIENTE
}
