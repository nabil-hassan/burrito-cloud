package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OrderResultSetExtractor implements ResultSetExtractor<List<OrderJDBC>> {

    @Override
    public List<OrderJDBC> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, OrderJDBC> orderMap = new HashMap<>();
        while (rs.next()) {
            long orderId = rs.getLong("order_id");
            long burritoId = rs.getLong("burrito_id");
            OrderJDBC order = orderMap.get(orderId);
            if (order == null) {
                order = extractOrder(orderId, rs);
                orderMap.put(orderId, order);
            }
            Optional<BurritoJDBC> burritoOpt = order.getBurritos().stream().filter(b -> b.getId().equals(burritoId)).findFirst();
            if (burritoOpt.isEmpty()) {
                BurritoJDBC newBurrito = extractBurrito(burritoId, rs);
                order.getBurritos().add(newBurrito);
                burritoOpt = Optional.of(newBurrito);
            }
            burritoOpt.get().getIngredients().add(rs.getString("ingredient_id"));
        }
        return new ArrayList<>(orderMap.values());
    }

    private OrderJDBC extractOrder(long id, ResultSet rs) throws SQLException {
        return OrderJDBC.builder().id(id).orderName(rs.getString("name"))
                .street(rs.getString("street")).town(rs.getString("town")).county(rs.getString("county")).postcode(rs.getString("postcode"))
                .creditCardCCV(rs.getString("ccccv")).creditCardNo(rs.getString("ccno")).creditCardExpiryDate(rs.getString("ccexpirydate"))
                .burritos(new ArrayList<>()).build();
    }

    private BurritoJDBC extractBurrito(long burritoId, ResultSet rs) throws SQLException {
        return BurritoJDBC.builder().id(burritoId).name(rs.getString("burrito_name")).ingredients(new ArrayList<>()).build();
    }
}
