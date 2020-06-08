package net.nh.burrito.repository;

import net.nh.burrito.entity.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Optional<Ingredient> findById(String id);
    List<Ingredient> findAll();
}
