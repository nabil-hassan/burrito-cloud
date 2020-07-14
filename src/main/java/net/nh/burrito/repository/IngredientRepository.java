package net.nh.burrito.repository;

import net.nh.burrito.entity.jdbc.IngredientJDBC;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Optional<IngredientJDBC> findById(String id);
    List<IngredientJDBC> findAll();
}
