package com.tienda.ecommerce.dto;

import java.util.Set;

public record UserDto(
        Long id,
        String name,
        String surname,
        String email,
        String avatarUrl,
        Set<String> roles,
        String address,
        String city,
        String postalCode,
        String province,
        String country
) {}
