package com.ecomerse.webecom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must be at most 200 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Size(max = 1000, message = "Image URL is too long")
        String imageUrl,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @NotNull(message = "Category is required")
        Long categoryId,

        @Size(max = 150, message = "Brand must be at most 150 characters")
        String brand,

        @Size(max = 150, message = "Warranty must be at most 150 characters")
        String warranty,

        List<String> pros,

        List<String> cons,

        Boolean active) {
}
