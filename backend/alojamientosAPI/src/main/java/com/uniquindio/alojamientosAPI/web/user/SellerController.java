package com.uniquindio.alojamientosAPI.web.user;

import com.uniquindio.alojamientosAPI.domain.dto.services.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.uniquindio.alojamientosAPI.domain.dto.SellerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    // ========== CREAR VENDEDOR ==========
    @Operation(summary = "Crear vendedor", description = "Crea un nuevo vendedor en el sistema. El email debe ser único")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<SellerDTO> createSeller(@RequestBody SellerDTO sellerDTO) {
        SellerDTO created = sellerService.createSeller(sellerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ========== OBTENER VENDEDOR POR ID ==========
    @Operation(summary = "Obtener vendedor por ID", description = "Devuelve la información de un vendedor específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vendedor no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSellerById(@PathVariable Long id) {
        SellerDTO seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }

    // ========== LISTAR TODOS LOS VENDEDORES ==========
    @Operation(summary = "Listar vendedores", description = "Devuelve el listado completo de vendedores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de vendedores obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<SellerDTO>> getAllSellers() {
        List<SellerDTO> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    // ========== ACTUALIZAR VENDEDOR ==========
    @Operation(summary = "Actualizar vendedor", description = "Actualiza los datos de un vendedor existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vendedor no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(@PathVariable Long id,
                                                  @RequestBody SellerDTO sellerDTO) {
        SellerDTO updated = sellerService.updateSeller(id, sellerDTO);
        return ResponseEntity.ok(updated);
    }

    // ========== ELIMINAR VENDEDOR ==========
    @Operation(summary = "Eliminar vendedor", description = "Elimina un vendedor del sistema por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vendedor eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Vendedor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
