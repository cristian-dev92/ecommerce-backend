package com.tienda.ecommerce.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) para manejar la solicitud de registro de usuarios.
 * Mapea los campos enviados desde el formulario de Angular.
 */

public record RegisterDto(String email, String password) {}

