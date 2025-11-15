package com.uniquindio.alojamientosAPI.domain.dto.services.ServicesImpl;

import com.uniquindio.alojamientosAPI.domain.dto.ProductDTO;
import com.uniquindio.alojamientosAPI.domain.dto.SellerDTO;
import com.uniquindio.alojamientosAPI.domain.dto.services.ProductService;
import com.uniquindio.alojamientosAPI.domain.dto.services.SellerService;
import com.uniquindio.alojamientosAPI.persistence.dao.ProductDAO;
import com.uniquindio.alojamientosAPI.persistence.entity.Product;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;
    private final SellerService sellerService;

    public ProductServiceImpl(ProductDAO productDAO, SellerService sellerService) {
        this.productDAO = productDAO;
        this.sellerService = sellerService;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateProductData(productDTO);

        // Verificar que el vendedor exista
        SellerDTO seller = sellerService.getSellerById(productDTO.getSellerId());
        if (seller == null) {
            throw new IllegalArgumentException("El vendedor no existe");
        }

        // Crear entidad
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(BigDecimal.valueOf(productDTO.getPrice() != null ?
                productDTO.getPrice() : null));
        product.setStock(productDTO.getStock());
        product.setSellerId(productDTO.getSellerId());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Guardar
        productDAO.save(product);

        // Retornar DTO completo
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().doubleValue(),
                product.getStock(),
                product.getSellerId(),
                seller.getName(),
                seller.getEmail(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        SellerDTO seller = sellerService.getSellerById(product.getSellerId());

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().doubleValue(),
                product.getStock(),
                product.getSellerId(),
                seller != null ? seller.getName() : null,
                seller != null ? seller.getEmail() : null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void validateProductData(ProductDTO productDTO) {
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (productDTO.getPrice() == null || productDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (productDTO.getStock() == null || productDTO.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

}
