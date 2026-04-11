package com.tienda.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tienda.ecommerce.auth.dto.UpdateAddressDto;
import com.tienda.ecommerce.model.Address;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

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

        // 2. Subir imagen a Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "avatars",          // Carpeta opcional en Cloudinary
                        "public_id", "avatar_" + userId, // Nombre único
                        "overwrite", true              // Reemplaza si ya existe
                )
        );

        // 3. Obtener URL segura
        String url = uploadResult.get("secure_url").toString();

        // 4. Guardar URL en BD
        user.setAvatarUrl(url);
        userRepository.save(user);

        // 5. Devolver URL al controlador
        return url;
    }

    public void deleteAccount(Long userId) {
        userRepository.deleteById(userId);
    }
    }
