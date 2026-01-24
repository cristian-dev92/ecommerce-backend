package com.tienda.ecommerce.auth.dto;

import com.tienda.ecommerce.model.User;

public record LoginResponse(String token, User user) {}
