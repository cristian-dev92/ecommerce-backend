package com.tienda.ecommerce.dto;

public record UpdateAddressDto(
        String address,
        String city,
        String postalCode,
        String province,
        String country
) {}