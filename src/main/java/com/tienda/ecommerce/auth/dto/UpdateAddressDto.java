package com.tienda.ecommerce.auth.dto;

public record UpdateAddressDto(
        String street,
        String city,
        String postalCode,
        String country
) {}
