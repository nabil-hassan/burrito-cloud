package net.nh.burrito.repository;

import net.nh.burrito.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order create(Order order);
    boolean update(Order order);
    List<Order> findAll();
    Optional<Order> findById(Long id);

}
