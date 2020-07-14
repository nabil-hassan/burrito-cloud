package net.nh.burrito.repository;

import net.nh.burrito.entity.jdbc.BurritoJDBC;

import java.util.List;
import java.util.Optional;

public interface BurritoRepository {
    Optional<BurritoJDBC> findById(Long id);
    List<BurritoJDBC> findAll();
    BurritoJDBC create(BurritoJDBC burrito);
    boolean update(BurritoJDBC burrito);
    boolean delete(Long id);
}
