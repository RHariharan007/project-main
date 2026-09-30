package com.ecomerse.webecom.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        int stockQuantity,
        Long categoryId,
        String categoryName,
        String brand,
        String warranty,
        List<String> pros,
        List<String> cons,
        double rating,
        int reviewCount,
        boolean active,
        LocalDateTime createdAt) {
}
