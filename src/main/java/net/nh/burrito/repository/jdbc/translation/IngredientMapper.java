package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.IngredientType;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IngredientMapper implements RowMapper<IngredientJDBC> {

    @Override
    public IngredientJDBC mapRow(ResultSet rs, int rowNum) throws SQLException {
        return IngredientJDBC.builder().id(rs.getString("id")).name(rs.getString("name"))
                .type(IngredientType.valueOf(rs.getString("type"))).build();
    }
}
