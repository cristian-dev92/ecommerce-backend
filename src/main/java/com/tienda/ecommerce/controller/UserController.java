package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.dto.UpdateAddressDto;
import com.tienda.ecommerce.dto.UpdateEmailDto;
import com.tienda.ecommerce.dto.UpdateNameDto;
import com.tienda.ecommerce.dto.UpdatePasswordDto;
import com.tienda.ecommerce.dto.UserDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Crucial para conectar con Angular sin fallos de CORS
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Devuelve el perfil del usuario autenticado de forma segura.
     * Mapea a UserDto para evitar enviar la contraseña cifrada a Angular.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.mapToDto(user));
    }

    /**
     * Actualiza el nombre y apellido del usuario.
     */
    @PutMapping("/update-name")
    public ResponseEntity<?> updateName(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateNameDto dto) {
        userService.updateName(user.getId(), dto.name(), dto.surname());
        return ResponseEntity.ok(Map.of("message", "Nombre y apellido actualizados con éxito"));
    }

    /**
     * Actualiza el correo electrónico de acceso.
     */
    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateEmailDto dto) {
        try {
            userService.updateEmail(user.getId(), dto.email());
            return ResponseEntity.ok(Map.of("message", "Email actualizado con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cambia la contraseña verificando la actual.
     */
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal User user,
            @RequestBody UpdatePasswordDto dto) {
        try {
            userService.updatePassword(user.getId(), dto.currentPassword(), dto.newPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualiza los datos de envío y facturación para Neon.
     */
    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateAddressDto dto) {
        userService.updateAddress(user.getId(), dto);
        return ResponseEntity.ok(Map.of("message", "Dirección de envío actualizada con éxito"));
    }

    /**
     * Guarda la URL del avatar seleccionada/subida desde Angular.
     */
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> updateAvatar(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> payload) {
        String newUrl = payload.get("avatarUrl");
        if (newUrl == null || newUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL de avatar no recibida"));
        }

        String savedUrl = userService.updateAvatar(user.getId(), newUrl);
        return ResponseEntity.ok(Map.of("avatarUrl", savedUrl));
    }

    /**
     * Permite al usuario dar de baja su propia cuenta.
     */
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal User user) {
        userService.deleteAccount(user.getId());
        return ResponseEntity.noContent().build();
    }
}