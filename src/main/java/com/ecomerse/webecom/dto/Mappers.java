package com.ecomerse.webecom.dto;

import com.ecomerse.webecom.entity.Address;
import com.ecomerse.webecom.entity.Cart;
import com.ecomerse.webecom.entity.CartItem;
import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.entity.Order;
import com.ecomerse.webecom.entity.OrderItem;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.Review;
import com.ecomerse.webecom.entity.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Converts entities to response DTOs so entities and passwords are never sent to the browser. */
public final class Mappers {

    private static final String SEPARATOR = "|";

    private Mappers() {
    }

    public static List<String> split(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        for (String part : text.split("\\|")) {
            if (!part.isBlank()) {
                result.add(part.trim());
            }
        }
        return result;
    }

    public static String join(List<String> values) {
        if (values == null) {
            return "";
        }
        List<String> cleaned = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                cleaned.add(value.trim().replace(SEPARATOR, "/"));
            }
        }
        return String.join(SEPARATOR, cleaned);
    }

    public static UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole());
    }

    public static CategoryResponse toCategory(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public static ProductResponse toProduct(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getStockQuantity(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getBrand(),
                p.getWarranty(),
                split(p.getPros()),
                split(p.getCons()),
                p.getAverageRating(),
                p.getReviewCount(),
                p.isActive(),
                p.getCreatedAt());
    }

    public static AddressResponse toAddress(Address a) {
        return new AddressResponse(a.getId(), a.getFullName(), a.getPhone(), a.getStreet(),
                a.getCity(), a.getState(), a.getPostalCode(), a.getCountry());
    }

    public static CartResponse toCart(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (CartItem item : cart.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
            count += item.getQuantity();
            items.add(new CartItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getImageUrl(),
                    item.getPrice(),
                    item.getQuantity(),
                    subtotal,
                    item.getProduct().getStockQuantity()));
        }
        return new CartResponse(cart.getId(), items, total, count);
    }

    public static OrderResponse toOrder(Order order) {
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            items.add(new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
        }
        return new OrderResponse(
                order.getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                items,
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getOrderDate());
    }

    public static ReviewResponse toReview(Review r) {
        return new ReviewResponse(r.getId(), r.getUser().getName(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
