package com.tienda.ecommerce.auth;

import com.tienda.ecommerce.dto.LoginDto;
import com.tienda.ecommerce.dto.LoginResponseDto;
import com.tienda.ecommerce.dto.RegisterDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.model.UserRole;
import com.tienda.ecommerce.repository.UserRepository;
import com.tienda.ecommerce.service.UserService;
import com.tienda.ecommerce.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Clave para la conexión con Angular
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Endpoint público para iniciar sesión.
     * Autentica contra Spring Security, genera el JWT y devuelve los datos seguros del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            // Devolvemos el token y el usuario transformado a DTO seguro sin password
            return ResponseEntity.ok(new LoginResponseDto(token, userService.mapToDto(user)));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }
    }

    /**
     * Endpoint público para crear nuevas cuentas desde Angular.
     * Encripta la contraseña y asigna el rol de cliente por defecto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto req) {
        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El email ya está registrado"));
        }

        // Construimos el nuevo usuario con el patrón Builder acoplado a Neon
        User newUser = User.builder()
                .name(req.name())
                .surname(req.surname())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .roles(Set.of(UserRole.ROLE_USER)) // Asignamos el enum dinámico por defecto
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        userRepository.save(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario registrado exitosamente"));
    }
}