package com.ecomerse.webecom.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "Shipping address is required")
        Long addressId) {
}
