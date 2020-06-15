package net.nh.burrito.repository;

import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;

import java.util.List;
import java.util.Optional;

public interface BurritoRepository {

    Burrito create(Burrito burrito);
    boolean update(Burrito order);
    List<Burrito> findAll();
    Optional<Burrito> findById();

}
