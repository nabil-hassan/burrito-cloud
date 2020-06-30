package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Ingredient;
import net.nh.burrito.entity.Ingredient.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BurritoResultSetExtractor implements ResultSetExtractor<List<Burrito>> {

    @Override
    public List<Burrito> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Burrito> idToValueMap = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("burrito_id");
            Burrito burrito = idToValueMap.get(id);
            if (burrito == null) {
                burrito = mapToBurrito(rs);
                idToValueMap.put(id, burrito);
            }
            Ingredient ingredient = mapToIngredient(rs);
            burrito.getIngredients().add(ingredient.getId());
        }
        return new ArrayList<>(idToValueMap.values());
    }

    private Ingredient mapToIngredient(ResultSet rs) throws SQLException {
        return Ingredient.builder().id(rs.getString("ingredient_id"))
                .name(rs.getString("ingredient_name"))
                .type(Type.valueOf(rs.getString("ingredient_type"))).build();
    }

    private Burrito mapToBurrito(ResultSet rs) throws SQLException {
        return Burrito.builder().id(rs.getLong("burrito_id")).name(rs.getString("burrito_name")).build();
    }


}
