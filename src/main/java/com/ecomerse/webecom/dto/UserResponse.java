package com.ecomerse.webecom.dto;

import com.ecomerse.webecom.entity.Role;

public record UserResponse(Long id, String name, String email, String phone, Role role) {
}
