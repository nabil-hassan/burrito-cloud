package net.nh.burrito.repository.jpa;

import net.nh.burrito.entity.jpa.OrderJPA;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepositoryJPA extends CrudRepository<OrderJPA, Long> {
}
