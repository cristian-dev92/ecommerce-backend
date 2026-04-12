package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.auth.dto.UpdateAddressDto;
import com.tienda.ecommerce.auth.dto.UserDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto user) {
        return ResponseEntity.ok("Usuario creado");
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        User user = (User) authentication.getPrincipal();
        String avatarUrl = userService.updateAvatar(user.getId(), file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }
    @PutMapping("/update-name")
    public ResponseEntity<?> updateName(@RequestBody Map<String, String> body, Authentication auth) {
        User user = (User) auth.getPrincipal();
        String newName = body.get("name");
        userService.updateName(user.getId(), newName);
        return ResponseEntity.ok(Map.of("message", "Nombre actualizado"));
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> body, Authentication auth) {
        User user = (User) auth.getPrincipal();
        String newEmail = body.get("email");
        userService.updateEmail(user.getId(), newEmail);
        return ResponseEntity.ok(Map.of("message", "Email actualizado"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body, Authentication auth) {
        User user = (User) auth.getPrincipal();
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        userService.updatePassword(user.getId(), currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada"));
    }

    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestBody UpdateAddressDto dto, Authentication auth) {
        User user = (User) auth.getPrincipal();
        userService.updateAddress(user.getId(), dto);
        return ResponseEntity.ok(Map.of("message", "Dirección actualizada"));
    }
}
