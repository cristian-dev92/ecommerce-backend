package com.tienda.ecommerce.auth;

import com.tienda.ecommerce.auth.dto.*;
import com.tienda.ecommerce.model.Address;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.UserPrincipal;
import com.tienda.ecommerce.service.UserService;
import com.tienda.ecommerce.security.JwtService;
import com.tienda.ecommerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Map;

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

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.email(),
                            req.password()
                    )
            );

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            User user = principal.getUser();
            String token = jwtService.generateToken(principal.getUser());

            return ResponseEntity.ok(new LoginResponseDto(token, user));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto req) {

        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email ya registrado"));
        }

        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));

        // Dirección vacía inicializada
        Address address = new Address();
        address.setFullName("");
        address.setStreet("");
        address.setCity("");
        address.setPostalCode("");
        address.setCountry("");

        user.setAddress(address);

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario registrado exitosamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PutMapping("update-name")
    public ResponseEntity<?> updateName(@RequestBody UpdateNameDto dto,
                                        @AuthenticationPrincipal User user) {
        userService.updateName(user.getId(), dto.name());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailDto dto,
                                         @AuthenticationPrincipal User user) {
        userService.updateEmail(user.getId(), dto.email());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto dto,
                                            @AuthenticationPrincipal User user) {
        userService.updatePassword(user.getId(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestBody UpdateAddressDto dto,
                                           @AuthenticationPrincipal User user) {
        userService.updateAddress(user.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                          @AuthenticationPrincipal User user)
    throws IOException {

        String avatarUrl = userService.updateAvatar(user.getId(), file);
        return ResponseEntity.ok().body( java.util.Map.of("avatarUrl", avatarUrl) );
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal User user) {
        userService.deleteAccount(user.getId());
        return ResponseEntity.ok().build();
    }
}