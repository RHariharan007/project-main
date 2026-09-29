package com.ecomerse.webecom.controller;

import com.ecomerse.webecom.dto.OrderRequest;
import com.ecomerse.webecom.dto.OrderResponse;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.service.OrderService;
import com.ecomerse.webecom.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request,
                                                    Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(user, request.addressId()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOne(@PathVariable Long id, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(orderService.getById(user, id));
    }
}
