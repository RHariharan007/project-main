package com.ecomerse.webecom.dto;

public record AddressResponse(
        Long id,
        String fullName,
        String phone,
        String street,
        String city,
        String state,
        String postalCode,
        String country) {
}
