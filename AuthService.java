package com.tienda.ecommerce.auth;

import com.tienda.ecommerce.auth.dto.LoginDto;
import com.tienda.ecommerce.auth.dto.RegisterDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio para la autenticación.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // PENDIENTE: Inyectar BCryptPasswordEncoder cuando configuremos Security

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
     * Valida las credenciales de un usuario.
     */
    public Optional<User> login(LoginDto request) {
        return userRepository.findByEmail(request.email())
                .filter(user -> false);
        // En el futuro, usar passwordEncoder.matches()
    }
}