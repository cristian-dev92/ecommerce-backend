package com.tienda.ecommerce.service;

import com.tienda.ecommerce.auth.dto.UpdateAddressDto;
import com.tienda.ecommerce.model.Address;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void updateName(Long userId, String newName) {
        User user = findById(userId); user.setName(newName);
        userRepository.save(user);
    }

    public void updateEmail(Long userId, String newEmail) {
        User user = findById(userId); user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void updateAddress(Long userId, UpdateAddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Address address = user.getAddress();
        if(address == null) {
            address = new Address();
            user.setAddress(address);
        }

        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setPostalCode(dto.postalCode());
        address.setCountry(dto.country());
        userRepository.save(user);
    }

    public String updateAvatar(Long userId, MultipartFile file) throws IOException {


        // 1. Obtener usuario
        User user = userRepository.findById(userId) .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear nombre único del archivo
        String fileName = "avatar_" + userId + ".png";

        // 3. Guardar archivo en carpeta /uploads
        Path uploadPath = Paths.get("uploads/" + fileName); Files.write(uploadPath, file.getBytes());

        // 4. Crear URL pública
        String url = "http://localhost:8080/uploads/" + fileName;

        // 5. Guardar URL en BD
        user.setAvatarUrl(url); userRepository.save(user);

        // 6. Devolver URL al controlador
        return url;
}

    public void deleteAccount(Long userId) {
        userRepository.deleteById(userId);
    }
}
