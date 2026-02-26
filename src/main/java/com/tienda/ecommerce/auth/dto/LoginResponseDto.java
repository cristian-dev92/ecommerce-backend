package com.tienda.ecommerce.auth.dto;

import com.tienda.ecommerce.model.User;

public record LoginResponseDto(String token, User user) {}
