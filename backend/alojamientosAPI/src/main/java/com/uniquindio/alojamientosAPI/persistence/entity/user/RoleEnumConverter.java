package com.uniquindio.alojamientosAPI.persistence.entity.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.text.Normalizer;

@Converter(autoApply = false)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case ADMINISTRADOR -> "Administrador";
            case CLIENTE -> "Cliente";
            case ANFITRION -> "Anfitrión";
        };
    }

    @Override
    public RoleEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String normalized = normalize(dbData);
        switch (normalized) {
            case "administrador", "admin" -> {
                return RoleEnum.ADMINISTRADOR;
            }
            case "cliente", "user", "usuario" -> {
                return RoleEnum.CLIENTE;
            }
            case "anfitrion", "anfitrión", "host" -> {
                return RoleEnum.ANFITRION;
            }
            default -> throw new IllegalArgumentException("Valor de rol no reconocido: " + dbData);
        }
    }

    private String normalize(String input) {
        String n = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
        return n;
    }
}

