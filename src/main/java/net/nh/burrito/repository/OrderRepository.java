package net.nh.burrito.repository;

import net.nh.burrito.entity.jdbc.OrderJDBC;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<OrderJDBC> findById(Long id);
    List<OrderJDBC> findAll();
    OrderJDBC create(OrderJDBC order);
    boolean update(OrderJDBC order);
    boolean delete(Long id);
}
