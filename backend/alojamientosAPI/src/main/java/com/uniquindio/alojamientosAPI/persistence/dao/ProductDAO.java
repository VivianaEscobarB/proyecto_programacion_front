package com.uniquindio.alojamientosAPI.persistence.dao;

import com.uniquindio.alojamientosAPI.persistence.entity.Product;

public interface ProductDAO {
    void save(Product product);
    Product findById(Long id);
}
