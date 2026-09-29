package com.ecomerse.webecom.repository;

import com.ecomerse.webecom.entity.Order;
import com.ecomerse.webecom.entity.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findAllByOrderByOrderDateDesc();

    boolean existsByUserIdAndItemsProductIdAndStatusNot(Long userId, Long productId, OrderStatus status);
}
