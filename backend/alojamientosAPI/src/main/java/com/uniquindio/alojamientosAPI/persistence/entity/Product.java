package com.uniquindio.alojamientosAPI.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;




import java.time.LocalDateTime;

@Setter
@Getter
public class Product {

    // Getters & Setters
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

