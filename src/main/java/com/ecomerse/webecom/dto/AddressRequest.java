package com.ecomerse.webecom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name is too long")
        String fullName,

        @NotBlank(message = "Phone is required")
        @Size(max = 20, message = "Phone is too long")
        String phone,

        @NotBlank(message = "Street is required")
        @Size(max = 250, message = "Street is too long")
        String street,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City is too long")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State is too long")
        String state,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code is too long")
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country is too long")
        String country) {
}
