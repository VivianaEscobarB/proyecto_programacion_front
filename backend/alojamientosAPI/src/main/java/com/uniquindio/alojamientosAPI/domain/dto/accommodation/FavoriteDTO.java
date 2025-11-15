package com.uniquindio.alojamientosAPI.domain.dto.accommodation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteDTO {
    private Long id;
    private Long userId;
    private Long accommodationId;
}

