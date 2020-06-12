package net.nh.burrito.repository.jdbc;

import net.nh.burrito.entity.Ingredient;
import net.nh.burrito.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Ingredient> findAll() {
        return jdbcTemplate.query("SELECT type, name, id FROM ingredient", this::mapToIngredient);
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.queryForObject("SELECT type, name, id FROM ingredient WHERE id = :id", namedParameters, this::mapToOptionalIngredient);
    }

    private Ingredient mapToIngredient(ResultSet resultSet, int i) throws SQLException {
        return Ingredient.builder().type(Ingredient.Type.valueOf(resultSet.getString("type")))
                .name(resultSet.getString("name")).id(resultSet.getString("id")).build();
    }

    private Optional<Ingredient> mapToOptionalIngredient(ResultSet rs, int i) throws SQLException {
        return Optional.ofNullable(mapToIngredient(rs, i));
    }
}
