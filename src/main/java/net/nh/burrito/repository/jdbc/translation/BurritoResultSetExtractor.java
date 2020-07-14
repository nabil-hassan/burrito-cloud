package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.entity.jdbc.IngredientJDBC.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BurritoResultSetExtractor implements ResultSetExtractor<List<BurritoJDBC>> {

    @Override
    public List<BurritoJDBC> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, BurritoJDBC> idToValueMap = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("burrito_id");
            BurritoJDBC burrito = idToValueMap.get(id);
            if (burrito == null) {
                burrito = mapToBurrito(rs);
                idToValueMap.put(id, burrito);
            }
            IngredientJDBC ingredient = mapToIngredient(rs);
            if (ingredient != null)
                burrito.getIngredients().add(ingredient.getId());
        }
        return new ArrayList<>(idToValueMap.values());
    }

    private IngredientJDBC mapToIngredient(ResultSet rs) throws SQLException {
        String id = rs.getString("ingredient_id");
        if (id == null) {
            return null;
        }
        String name = rs.getString("ingredient_name");
        String type = rs.getString("ingredient_type");
        return IngredientJDBC.builder().id(id)
                .name(name)
                .type(Type.valueOf(type)).build();
    }

    private BurritoJDBC mapToBurrito(ResultSet rs) throws SQLException {
        return BurritoJDBC.builder().id(rs.getLong("burrito_id")).name(rs.getString("burrito_name")).build();
    }


}
