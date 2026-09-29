package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.dto.OrderResponse;
import com.ecomerse.webecom.entity.Address;
import com.ecomerse.webecom.entity.Cart;
import com.ecomerse.webecom.entity.CartItem;
import com.ecomerse.webecom.entity.Order;
import com.ecomerse.webecom.entity.OrderItem;
import com.ecomerse.webecom.entity.OrderStatus;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.Role;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.AddressRepository;
import com.ecomerse.webecom.repository.CartRepository;
import com.ecomerse.webecom.repository.OrderRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        AddressRepository addressRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    /** Turns the user's cart into an order, reduces stock and empties the cart. */
    public OrderResponse createOrder(User user, Long addressId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(formatAddress(address));

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive()) {
                throw new BadRequestException(product.getName() + " is no longer available");
            }
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        Order saved = orderRepository.saveAndFlush(order);

        cart.getItems().clear();
        cartRepository.save(cart);
        return Mappers.toOrder(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId())
                .stream().map(Mappers::toOrder).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream().map(Mappers::toOrder).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(User user, Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        boolean isOwner = order.getUser().getId().equals(user.getId());
        if (!isOwner && user.getRole() != Role.ADMIN) {
            throw new ResourceNotFoundException("Order not found");
        }
        return Mappers.toOrder(order);
    }

    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        OrderStatus oldStatus = order.getStatus();
        if (oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            throw new BadRequestException("A cancelled order cannot be reopened");
        }
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
        order.setStatus(newStatus);
        return Mappers.toOrder(orderRepository.save(order));
    }

    private String formatAddress(Address a) {
        return a.getFullName() + ", " + a.getStreet() + ", " + a.getCity() + ", " + a.getState()
                + " " + a.getPostalCode() + ", " + a.getCountry() + " (Phone: " + a.getPhone() + ")";
    }
}
