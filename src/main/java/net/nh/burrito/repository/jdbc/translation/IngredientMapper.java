package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.Ingredient;
import net.nh.burrito.entity.Ingredient.Type;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IngredientMapper implements RowMapper<Ingredient> {

    @Override
    public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Ingredient.builder().id(rs.getString("id")).name(rs.getString("name"))
                .type(Type.valueOf(rs.getString("type"))).build();
    }
}
