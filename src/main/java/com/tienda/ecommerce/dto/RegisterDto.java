package com.tienda.ecommerce.dto;


/**
 * Data Transfer Object (DTO) para manejar la solicitud de registro de usuarios.
 * Mapea los campos enviados desde el formulario de Angular.
 */

public record RegisterDto(String name, String surname, String email, String password) {}

