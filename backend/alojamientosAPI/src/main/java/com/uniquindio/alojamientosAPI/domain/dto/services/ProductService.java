package com.uniquindio.alojamientosAPI.domain.dto.services;

import com.uniquindio.alojamientosAPI.domain.dto.ProductDTO;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
}

