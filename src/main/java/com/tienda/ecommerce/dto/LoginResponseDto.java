package com.tienda.ecommerce.dto;

import com.tienda.ecommerce.model.User;

public record LoginResponseDto(String token, UserDto user) {}
