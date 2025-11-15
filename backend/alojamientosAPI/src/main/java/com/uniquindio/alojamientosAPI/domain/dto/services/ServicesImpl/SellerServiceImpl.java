package com.uniquindio.alojamientosAPI.domain.dto.services.ServicesImpl;



import com.uniquindio.alojamientosAPI.domain.dto.SellerDTO;
import com.uniquindio.alojamientosAPI.domain.dto.services.SellerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SellerServiceImpl implements SellerService {

    private final Map<Long, SellerDTO> sellerDB = new HashMap<>();

    public SellerServiceImpl() {
        // Datos simulados de ejemplo
        sellerDB.put(1L, new SellerDTO(1L, "María González Tech", "maria.gonzalez@techstore.com"));
        sellerDB.put(2L, new SellerDTO(2L, "Carlos Pérez", "carlos.perez@ventas.com"));
    }

    @Override
    public SellerDTO getSellerById(Long id) {
        return sellerDB.get(id);
    }

    @Override
    public SellerDTO createSeller(SellerDTO sellerDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSeller'");
    }

    @Override
    public List<SellerDTO> getAllSellers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllSellers'");
    }

    @Override
    public SellerDTO updateSeller(Long id, SellerDTO sellerDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSeller'");
    }

    @Override
    public void deleteSeller(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteSeller'");
    }
}

