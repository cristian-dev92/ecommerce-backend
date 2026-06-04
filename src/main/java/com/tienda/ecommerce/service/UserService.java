package com.tienda.ecommerce.service;

import com.tienda.ecommerce.dto.UpdateAddressDto;
import com.tienda.ecommerce.dto.UserDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método VITAL para Spring Security.
     * Permite buscar al usuario en Neon por su email durante el proceso de Login.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public void updateName(Long userId, String newName, String newSurname) {
        User user = findById(userId);
        user.setName(newName);
        user.setSurname(newSurname);
        user.setUpdatedAt(LocalDateTime.now());
        user.setSurname(newSurname);
        userRepository.save(user);
    }

    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        User user = findById(userId);
        // Evitamos que cambie su correo a uno que ya use otra persona
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("El email ya está registrado por otro usuario");
        }
        user.setEmail(newEmail);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateAddress(Long userId, UpdateAddressDto dto) {
        User user = findById(userId);

        // Mapeamos los campos planos directamente a la entidad unificada
        user.setAddress(dto.address());
        user.setCity(dto.city());
        user.setPostalCode(dto.postalCode());
        user.setProvince(dto.province());
        user.setCountry(dto.country());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Transactional
    public String updateAvatar(Long userId, String avatarUrl) {
        User user = findById(userId);
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return avatarUrl;
    }

    @Transactional
    public void deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(userId);
    }

    // Helper para transformar la entidad User en un DTO seguro para Angular
    public UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
