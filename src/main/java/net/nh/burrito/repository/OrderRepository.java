package net.nh.burrito.repository;

import net.nh.burrito.entity.Order;

public interface OrderRepository {

    Order save(Order order);

}
