package net.nh.burrito.repository.jdbc;

import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.repository.IngredientRepository;
import net.nh.burrito.repository.jdbc.translation.IngredientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class IngredientRepositoryJDBC implements IngredientRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final IngredientMapper ingredientMapper;

    @Autowired
    public IngredientRepositoryJDBC(NamedParameterJdbcTemplate jdbcTemplate, IngredientMapper ingredientMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public List<IngredientJDBC> findAll() {
        return jdbcTemplate.query("SELECT type, name, id FROM ingredient", ingredientMapper);
    }

    @Override
    public Optional<IngredientJDBC> findById(String id) {
        try {
            IngredientJDBC ingredient = jdbcTemplate.queryForObject("SELECT type, name, id FROM ingredient WHERE id = :id", Map.of("id", id), ingredientMapper);
            return Optional.of(ingredient);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
