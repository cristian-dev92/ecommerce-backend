package com.tienda.ecommerce.auth;

import com.tienda.ecommerce.auth.dto.*;
import com.tienda.ecommerce.model.Address;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.UserService;
import com.tienda.ecommerce.security.JwtService;
import com.tienda.ecommerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para manejar las operaciones de autenticación (Registro e Inicio de Sesión).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        User user = userRepository.findByEmail(req.email()).get();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginDto(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email ya registrado");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));

        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado");
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailDto req, @AuthenticationPrincipal User user){
        userService.updateEmail(user.getId(), req.email());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto req, @AuthenticationPrincipal User user) {
        userService.updatePassword(user.getId(), req.currentPassword(), req.newPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestBody Address address, @AuthenticationPrincipal User user) {
        userService.updateAddress(user.getId(), address);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file, @AuthenticationPrincipal User user)
    throws IOException {
        String folder = "uploads/";
        Files.createDirectories(Paths.get(folder));
        String filePath = folder + file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(filePath));
        userService.updateAvatar(user.getId(), filePath);
        return ResponseEntity.ok().body( java.util.Map.of("avatarUrl", filePath) );
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal User user) {
        userService.deleteAccount(user.getId());
        return ResponseEntity.ok().build();
    }
}