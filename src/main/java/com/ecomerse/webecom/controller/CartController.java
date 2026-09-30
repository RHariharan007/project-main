package com.ecomerse.webecom.controller;

import com.ecomerse.webecom.dto.CartItemRequest;
import com.ecomerse.webecom.dto.CartResponse;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.service.CartService;
import com.ecomerse.webecom.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(currentUser(authentication)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemRequest request,
                                                Authentication authentication) {
        return ResponseEntity.ok(cartService.addItem(
                currentUser(authentication), request.productId(), request.quantity()));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long productId,
                                                   @Valid @RequestBody CartItemRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(cartService.updateQuantity(
                currentUser(authentication), productId, request.quantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId, Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItem(currentUser(authentication), productId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clear(Authentication authentication) {
        return ResponseEntity.ok(cartService.clear(currentUser(authentication)));
    }

    private User currentUser(Authentication authentication) {
        return userService.getByEmail(authentication.getName());
    }
}
