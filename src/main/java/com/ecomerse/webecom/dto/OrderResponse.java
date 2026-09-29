package com.ecomerse.webecom.dto;

import com.ecomerse.webecom.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        OrderStatus status,
        String shippingAddress,
        LocalDateTime orderDate) {
}
