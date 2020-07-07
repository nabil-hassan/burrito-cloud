package net.nh.burrito.repository;

import net.nh.burrito.entity.Burrito;

import java.util.List;
import java.util.Optional;

public interface BurritoRepository {
    Optional<Burrito> findById(Long id);
    List<Burrito> findAll();
    Burrito create(Burrito burrito);
    boolean update(Burrito burrito);
    boolean delete(Long id);
}
