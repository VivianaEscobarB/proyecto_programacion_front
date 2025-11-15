package com.uniquindio.alojamientosAPI.domain.dto.accommodation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PictureDTO {
    private Long id; // cambiado de Integer a Long
    private String title;
    private String description;
    private Boolean isMain;
    private String url;
}
