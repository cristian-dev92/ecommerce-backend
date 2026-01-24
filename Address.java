package com.tienda.ecommerce.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Address {

    // Getters y setters
    private String fullName;
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public Address() {}

    public Address(String fullName, String street, String city, String postalCode, String country) {
        this.fullName = fullName;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }
}


