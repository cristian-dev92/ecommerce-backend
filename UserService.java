package com.tienda.ecommerce.service;

import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.model.Address;
import com.tienda.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User updateEmail(Long userId, String newEmail) {
        User user = findById(userId); user.setEmail(newEmail);
        return userRepository.save(user);
    }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User updateAddress(Long userId, Address address) {
        User user = findById(userId);
        user.setAddress(address);
        return userRepository.save(user);
    }

    public User updateAvatar(Long userId, String avatarUrl) {
        User user = findById(userId); user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }
    public void deleteAccount(Long userId) {
        userRepository.deleteById(userId);
    }
}
