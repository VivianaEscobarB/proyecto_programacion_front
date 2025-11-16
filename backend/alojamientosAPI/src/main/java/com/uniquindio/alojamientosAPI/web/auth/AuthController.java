package com.uniquindio.alojamientosAPI.web.auth;

import com.uniquindio.alojamientosAPI.persistence.repository.UserRepository;
import com.uniquindio.alojamientosAPI.security.auth.CustomUserDetails;
import com.uniquindio.alojamientosAPI.security.jwt.JwtService;
import com.uniquindio.alojamientosAPI.web.auth.dto.AuthResponse;
import com.uniquindio.alojamientosAPI.web.auth.dto.LoginRequest;
import com.uniquindio.alojamientosAPI.domain.service.user.UserQueryService;
import com.uniquindio.alojamientosAPI.web.auth.dto.UpdateUserProfileRequest;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final com.uniquindio.alojamientosAPI.domain.service.user.UserRegistrationService userRegistrationService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          com.uniquindio.alojamientosAPI.domain.service.user.UserRegistrationService userRegistrationService,
                          UserQueryService userQueryService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRegistrationService = userRegistrationService;
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(principal);
            long expiresInMs = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();
            List<String> roles = principal.getSimpleRoleNames(); // roles simples
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + expiresInMs))
                    .roles(roles)
                    .email(principal.getUsername())
                    .urlAccountPhoto(principal.getUrlAccountPhoto())
                    .userId(principal.getUser().getId())
                    .build());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid com.uniquindio.alojamientosAPI.domain.dto.user.RegisterUserCommand command) {
        try {
            var result = userRegistrationService.register(command);
            return ResponseEntity.status(201).body(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        return userQueryService.findUserDetailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/by-email/{email}")
    public ResponseEntity<?> getUserDetailsByEmail(@PathVariable String email) {
        return userQueryService.findUserDetailByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(
                java.util.Map.of(
                        "email", principal.getUsername(),
                        "roles", principal.getAuthorities()
                )
        );
    }

    @GetMapping("/debug-token")
    public ResponseEntity<?> debugToken(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing Bearer token"));
        }
        String token = authHeader.substring(7);
        try {
            Claims all = jwtService.extractAllClaims(token);
            return ResponseEntity.ok(Map.of(
                    "subject", all.getSubject(),
                    "expires", all.getExpiration(),
                    "issuedAt", all.getIssuedAt()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/user/{id}/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePhoto(@PathVariable Long id,
                                         @RequestBody Map<String, String> body,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null || principal.getUser().getId() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        Long currentId = principal.getUser().getId();
        if (!currentId.equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "No puedes actualizar el perfil de otro usuario"));
        }
        String url = body.get("urlAccountPhoto");
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "urlAccountPhoto requerido"));
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setUrlAccountPhoto(url);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("urlAccountPhoto", user.getUrlAccountPhoto()));
    }

    @PostMapping("/user/{id}/photo/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null || principal.getUser().getId() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        Long currentId = principal.getUser().getId();
        if (!currentId.equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "No puedes actualizar el perfil de otro usuario"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo requerido"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato inválido. Usa JPG, PNG o WEBP"));
        }
        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String ext = switch (contentType) {
                case "image/png" -> ".png"; case "image/webp" -> ".webp"; default -> ".jpg"; };
            String filename = "user-" + id + "-" + UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(filename)
                    .toUriString();
            var user = userRepository.findById(id).orElse(null);
            if (user == null) return ResponseEntity.notFound().build();
            user.setUrlAccountPhoto(url);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("urlAccountPhoto", url));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @RequestBody UpdateUserProfileRequest body,
                                           @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null || principal.getUser() == null || principal.getUser().getId() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        Long currentId = principal.getUser().getId();
        if (!currentId.equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "No puedes actualizar el perfil de otro usuario"));
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        if (body.getPhoneNumber() != null) user.setPhoneNumber(body.getPhoneNumber().trim());
        if (body.getHomeAddress() != null) user.setHomeAddress(body.getHomeAddress().trim());
        if (body.getDayOfBirth() != null && !body.getDayOfBirth().isBlank()) {
            try {
                user.setDayOfBirth(LocalDate.parse(body.getDayOfBirth().trim()));
            } catch (Exception ignored) {}
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
                "phoneNumber", user.getPhoneNumber(),
                "homeAddress", user.getHomeAddress(),
                "dayOfBirth", user.getDayOfBirth()
        ));
    }
}
