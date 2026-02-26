package com.tienda.ecommerce.auth.dto;

/**
 * Data Transfer Object (DTO) para manejar la solicitud de inicio de sesión.
 * Mapea los campos enviados desde el formulario de Angular.
 */

public record LoginDto (String name, String email, String password) {}

