package com.ecomerse.webecom.controller;

import com.ecomerse.webecom.dto.ProductResponse;
import com.ecomerse.webecom.dto.ReviewRequest;
import com.ecomerse.webecom.dto.ReviewResponse;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.service.ProductService;
import com.ecomerse.webecom.service.ReviewService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final UserService userService;

    public ProductController(ProductService productService, ReviewService reviewService, UserService userService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    /** Optional filters: keyword, categoryId, sort (price_asc, price_desc, rating, newest, name). */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(productService.search(keyword, categoryId, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(productService.search(keyword, categoryId, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getPublicById(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponse>> reviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getForProduct(id));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long id,
                                                    @Valid @RequestBody ReviewRequest request,
                                                    Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addOrUpdate(user, id, request));
    }
}
