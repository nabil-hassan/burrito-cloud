package net.nh.burrito.repository.jdbc.translation;

import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
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
import java.util.Set;

@Component
public class OrderResultSetExtractor implements ResultSetExtractor<List<Order>> {

    @Override
    public List<Order> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Order> orderMap = new HashMap<>();
        while (rs.next()) {
            long orderId = rs.getLong("order_id");
            long burritoId = rs.getLong("burrito_id");
            Order order = orderMap.get(orderId);
            if (order == null) {
                order = extractOrder(orderId, rs);
                orderMap.put(orderId, order);
            }
            Optional<Burrito> burritoOpt = order.getBurritos().stream().filter(b -> b.getId().equals(burritoId)).findFirst();
            if (burritoOpt.isEmpty()) {
                Burrito newBurrito = extractBurrito(burritoId, rs);
                order.getBurritos().add(newBurrito);
                burritoOpt = Optional.of(newBurrito);
            }
            burritoOpt.get().getIngredients().add(rs.getString("ingredient_id"));
        }
        return new ArrayList<>(orderMap.values());
    }

    private Order extractOrder(long id, ResultSet rs) throws SQLException {
        return Order.builder().id(id).orderName(rs.getString("name"))
                .street(rs.getString("street")).town(rs.getString("town")).county(rs.getString("county")).postcode(rs.getString("postcode"))
                .creditCardCCV(rs.getString("ccccv")).creditCardNo(rs.getString("ccno")).creditCardExpiryDate(rs.getString("ccexpirydate"))
                .burritos(new ArrayList<>()).build();
    }

    private Burrito extractBurrito(long burritoId, ResultSet rs) throws SQLException {
        return Burrito.builder().id(burritoId).name(rs.getString("burrito_name")).ingredients(new ArrayList<>()).build();
    }
}
