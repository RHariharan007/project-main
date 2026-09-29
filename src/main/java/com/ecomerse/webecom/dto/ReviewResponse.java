package com.ecomerse.webecom.dto;

import java.time.LocalDateTime;

public record ReviewResponse(Long id, String userName, int rating, String comment, LocalDateTime createdAt) {
}
