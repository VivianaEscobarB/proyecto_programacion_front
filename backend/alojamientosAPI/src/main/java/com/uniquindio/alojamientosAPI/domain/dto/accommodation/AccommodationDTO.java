package com.uniquindio.alojamientosAPI.domain.dto.accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDTO {
    private Long id; // cambiado de Integer a Long
    private String title;
    private String description;
    private String address;
    private Double dayPrice;
    private Integer capacity;
    private String stateName;
    private String cityName;
    private String hostName;
    private String hostPhotoUrl;
    private String mainPictureUrl; // nueva propiedad
    private List<PictureDTO> pictures;
    private List<ServiceDTO> services;
}
