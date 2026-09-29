package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.CartResponse;
import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.entity.Cart;
import com.ecomerse.webecom.entity.CartItem;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.CartRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartResponse getCart(User user) {
        return Mappers.toCart(getOrCreateCart(user));
    }

    public CartResponse addItem(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = findActiveProduct(productId);
        CartItem existing = findItem(cart, productId);
        int newQuantity = quantity + (existing == null ? 0 : existing.getQuantity());
        checkStock(product, newQuantity);

        if (existing == null) {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            cart.getItems().add(item);
        } else {
            existing.setQuantity(newQuantity);
        }
        return Mappers.toCart(cartRepository.saveAndFlush(cart));
    }

    public CartResponse updateQuantity(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new ResourceNotFoundException("This product is not in your cart");
        }
        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            checkStock(item.getProduct(), quantity);
            item.setQuantity(quantity);
        }
        return Mappers.toCart(cartRepository.saveAndFlush(cart));
    }

    public CartResponse removeItem(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new ResourceNotFoundException("This product is not in your cart");
        }
        cart.getItems().remove(item);
        return Mappers.toCart(cartRepository.saveAndFlush(cart));
    }

    public CartResponse clear(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        return Mappers.toCart(cartRepository.saveAndFlush(cart));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartItem findItem(Cart cart, Long productId) {
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private Product findActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return product;
    }

    private void checkStock(Product product, int wantedQuantity) {
        if (wantedQuantity > product.getStockQuantity()) {
            throw new BadRequestException("Only " + product.getStockQuantity()
                    + " unit(s) of " + product.getName() + " are in stock");
        }
    }
}
