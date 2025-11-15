package com.uniquindio.alojamientosAPI.web.accommodation;

import com.uniquindio.alojamientosAPI.domain.service.accommodation.FavoriteService;
import com.uniquindio.alojamientosAPI.persistence.entity.accommodation.FavoriteEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @PostMapping
    public ResponseEntity<FavoriteEntity> addFavorite(@AuthenticationPrincipal CustomUserDetails principal,
                                                      @RequestBody FavoriteEntity favorite) {
        Long currentUserId = requireUserId(principal);
        // Forzar ownership al usuario autenticado
        UserEntity owner = UserEntity.builder().id(currentUserId).build();
        favorite.setUser(owner);
        favorite.setId(null);
        return ResponseEntity.ok(favoriteService.addFavorite(favorite));
    }

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @GetMapping
    public ResponseEntity<List<FavoriteEntity>> getMyFavorites(@AuthenticationPrincipal CustomUserDetails principal) {
        Long currentUserId = requireUserId(principal);
        return ResponseEntity.ok(favoriteService.findByUser(currentUserId));
    }

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteEntity>> getFavoritesByUser(@AuthenticationPrincipal CustomUserDetails principal,
                                                                   @PathVariable Long userId) {
        Long currentUserId = requireUserId(principal);
        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes consultar favoritos de otro usuario");
        }
        return ResponseEntity.ok(favoriteService.findByUser(userId));
    }

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<List<FavoriteEntity>> getFavoritesByAccommodation(@AuthenticationPrincipal CustomUserDetails principal,
                                                                            @PathVariable Long accommodationId) {
        // Opcionalmente, podrías validar que haya favoritos del usuario sobre ese alojamiento
        // y devolver solo los del usuario autenticado; por ahora, devolvemos la lista filtrada por alojamiento.
        return ResponseEntity.ok(favoriteService.findByAccommodation(accommodationId));
    }

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal CustomUserDetails principal,
                                               @PathVariable Long id) {
        Long currentUserId = requireUserId(principal);
        var fav = favoriteService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Favorito no encontrado"));
        Long ownerId = fav.getUser() != null ? fav.getUser().getId() : null;
        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes eliminar favoritos de otro usuario");
        }
        favoriteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('CLIENTE','ANFITRION')")
    @GetMapping("/my/accommodation/{accommodationId}")
    public ResponseEntity<Map<String, Boolean>> isMyFavorite(@AuthenticationPrincipal CustomUserDetails principal,
                                                             @PathVariable Long accommodationId) {
        Long currentUserId = requireUserId(principal);
        boolean exists = favoriteService.findByUser(currentUserId).stream()
                .anyMatch(f -> f.getAccommodation()!=null && accommodationId.equals(f.getAccommodation().getId()));
        return ResponseEntity.ok(Map.of("favorite", exists));
    }

    private Long requireUserId(CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null || principal.getUser().getId() == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return principal.getUser().getId();
    }
}
