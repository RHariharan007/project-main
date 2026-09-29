package com.ecomerse.webecom.dto;

import com.ecomerse.webecom.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status) {
}
