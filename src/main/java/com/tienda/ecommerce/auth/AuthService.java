package com.tienda.ecommerce.auth;

import com.tienda.ecommerce.auth.dto.LoginDto;
import com.tienda.ecommerce.auth.dto.RegisterDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio para la autenticación.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public User register(RegisterDto request) throws Exception {
        if (userRepository.existsByEmail(request.email())) {
            throw new Exception("El email ya está registrado");
        }

        User user = new User();
        user.setEmail(request.email());
        // IMPORTANTE: Aquí se debería encriptar la contraseña
        user.setPassword(request.password());

        return userRepository.save(user);
    }

    /**
     * Credenciales de usuario.
     */
    public Optional<User> login(LoginDto request) {
        return userRepository.findByEmail(request.email())
                .filter(user -> false);
        // En el futuro, usar passwordEncoder.matches()
    }

    public ResponseEntity<?> updateName(String email, String newName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setName(newName);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message",
                "Nombre actualizado",
                "name", newName ));
    }
}