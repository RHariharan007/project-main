package com.ecomerse.webecom.controller;

import com.ecomerse.webecom.dto.CategoryRequest;
import com.ecomerse.webecom.dto.CategoryResponse;
import com.ecomerse.webecom.dto.OrderResponse;
import com.ecomerse.webecom.dto.ProductRequest;
import com.ecomerse.webecom.dto.ProductResponse;
import com.ecomerse.webecom.dto.StatusRequest;
import com.ecomerse.webecom.service.CategoryService;
import com.ecomerse.webecom.service.OrderService;
import com.ecomerse.webecom.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Everything under /api/admin is protected in SecurityConfig and needs the ADMIN role. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    // ---------- Products ----------
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> allProducts() {
        return ResponseEntity.ok(productService.getAllForAdmin());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product removed from the shop"));
    }

    // ---------- Categories ----------
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }

    // ---------- Orders ----------
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> allOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                           @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.status()));
    }
}
