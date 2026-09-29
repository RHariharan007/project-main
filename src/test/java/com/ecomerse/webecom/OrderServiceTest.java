package com.ecomerse.webecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecomerse.webecom.dto.OrderResponse;
import com.ecomerse.webecom.entity.Address;
import com.ecomerse.webecom.entity.Cart;
import com.ecomerse.webecom.entity.CartItem;
import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.entity.Order;
import com.ecomerse.webecom.entity.OrderStatus;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.repository.AddressRepository;
import com.ecomerse.webecom.repository.CartRepository;
import com.ecomerse.webecom.repository.OrderRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import com.ecomerse.webecom.service.OrderService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user() {
        User user = new User();
        user.setId(1L);
        user.setName("Asha");
        user.setEmail("asha@example.com");
        return user;
    }

    private Product product(int stock) {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        Product product = new Product();
        product.setId(10L);
        product.setName("Speaker");
        product.setPrice(new BigDecimal("1000"));
        product.setStockQuantity(stock);
        product.setActive(true);
        product.setCategory(category);
        return product;
    }

    private Cart cartWith(User user, Product product, int quantity) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        cart.getItems().add(item);
        return cart;
    }

    @Test
    void createOrderFailsWhenCartIsEmpty() {
        User user = user();
        Cart emptyCart = new Cart();
        emptyCart.setUser(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(user, 5L));
    }

    @Test
    void createOrderFailsWhenStockIsTooLow() {
        User user = user();
        Product product = product(1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cartWith(user, product, 3)));
        when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(new Address()));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(user, 5L));
    }

    @Test
    void createOrderCalculatesTotalReducesStockAndClearsCart() {
        User user = user();
        Product product = product(10);
        Cart cart = cartWith(user, product, 2);
        Address address = new Address();
        address.setFullName("Asha");
        address.setPhone("999");
        address.setStreet("1 Main Road");
        address.setCity("Chennai");
        address.setState("Tamil Nadu");
        address.setPostalCode("600001");
        address.setCountry("India");

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(address));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        OrderResponse response = orderService.createOrder(user, 5L);

        assertEquals(new BigDecimal("2000"), response.totalAmount());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(8, product.getStockQuantity());
        assertEquals(0, cart.getItems().size());
    }
}
