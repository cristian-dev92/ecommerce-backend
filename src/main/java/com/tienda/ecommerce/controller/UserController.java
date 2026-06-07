package com.tienda.ecommerce.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Crucial para conectar con Angular sin fallos de CORS
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

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
     * OPCIÓN 1: Subir imagen desde el PC (Archivo Binario)
     * Angular enviará un FormData con el archivo binario en la clave "file".
     */
    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatarFromFile(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se ha recibido ningún archivo válido"));
        }

        try {
            // 1. Subimos los bytes del archivo a Cloudinary usando su API nativa
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Extraemos la URL segura (https) que nos devuelve Cloudinary
            String cloudinaryUrl = uploadResult.get("secure_url").toString();

            // 2. Guardamos la URL devuelta en tu base de datos de Neon
            String savedUrl = userService.updateAvatar(user.getId(), cloudinaryUrl);

            // 3. Devolvemos la URL a Angular
            return ResponseEntity.ok(Map.of("avatarUrl", savedUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar y subir el archivo: " + e.getMessage()));
        }
    }

    /**
     * OPCIÓN 2: Guardar avatar mediante URL directa (Texto)
     * Angular enviará un JSON plano: { "avatarUrl": "https://..." }
     */
    @PostMapping(value = "/upload-avatar-url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAvatarFromUrl(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> payload) {

        String newUrl = payload.get("avatarUrl");
        if (newUrl == null || newUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL de avatar no recibida"));
        }

        try {
            // Como ya es una URL de texto, se la pasamos directamente al servicio de base de datos
            String savedUrl = userService.updateAvatar(user.getId(), newUrl.trim());
            return ResponseEntity.ok(Map.of("avatarUrl", savedUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar la URL del avatar: " + e.getMessage()));
        }
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