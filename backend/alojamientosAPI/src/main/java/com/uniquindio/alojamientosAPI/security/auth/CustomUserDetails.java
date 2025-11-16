package com.uniquindio.alojamientosAPI.security.auth;

import com.uniquindio.alojamientosAPI.persistence.entity.user.RoleEntity;
import com.uniquindio.alojamientosAPI.persistence.entity.user.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetails para integrar la entidad UserEntity
 * con el sistema de autenticación de Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    /**
     * Convierte los roles del usuario en autoridades compatibles con Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<RoleEntity> roles = user.getRoles();

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name())) // ROLE_CLIENTE, ROLE_ANFITRION
                .collect(Collectors.toSet());
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Puede personalizarse si tu entidad maneja fechas de expiración
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Puedes usar un campo "locked" en la entidad si lo deseas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Ideal para tokens o credenciales temporales
    }

    @Override
    public boolean isEnabled() {
        return true; // Puedes vincularlo a un campo "active" en tu entidad
    }

    public UserEntity getUser() {
        return user;
    }

    public String getUrlAccountPhoto() {
        System.out.println("usuario: " + user.getPhoneNumber());
        System.out.println("usuario: " + user.getUrlAccountPhoto());
        System.out.println("usuario: " + user.getEmail());
        System.out.println("usuario: " + user.getFirstName());
        System.out.println("usuario: " + user.getLastName());
        System.out.println("usuario: " + user.getDayOfBirth());
        System.out.println("usuario: " + user.getRoles());
        System.out.println("usuario: " + user.getHomeAddress());
        System.out.println("usuario: " + user.getPassword());
        return user.getUrlAccountPhoto();
    }

    // Nuevo método para obtener nombres simples (sin prefijo) si el controlador los necesita
    public List<String> getSimpleRoleNames() {
        return user.getRoles().stream().map(r -> r.getName().name()).toList();
    }
}
