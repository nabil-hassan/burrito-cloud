package net.nh.burrito.repository;

import net.nh.burrito.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Order create(Order order);
    boolean update(Order order);
    boolean delete(Long id);
}
