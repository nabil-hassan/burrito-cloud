package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
import net.nh.burrito.repository.OrderRepository;
import net.nh.burrito.repository.jdbc.translation.OrderResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcOrderRepository implements OrderRepository {

    public static final String FIND_ALL_QUERY = "SELECT o.id as order_id, o.name, o.street, o.town, o.county, o.postcode, o.ccno, o.ccexpirydate, o.ccccv, " +
            "b.id as burrito_id, b.name as burrito_name, i.id as ingredient_id \n" +
            "FROM orders o\n" +
            "INNER JOIN order_burritos ob on ob.order_id = o.id\n" +
            "INNER JOIN burrito b on ob.burrito_id = b.id\n" +
            "INNER JOIN burrito_ingredients bi ON b.id = bi.burrito_id\n" +
            "INNER JOIN ingredient i ON i.id = bi.ingredient_id";
    public static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + "\n" + "WHERE o.id = :id";
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert insertOrderTemplate;
    private final SimpleJdbcInsert insertOrderBurritoTemplate;
    private final OrderResultSetExtractor resultSetExtractor;

    @Autowired
    public JdbcOrderRepository(NamedParameterJdbcTemplate template, DataSource dataSource, OrderResultSetExtractor resultSetExtractor) {
        this.template = template;
        this.insertOrderTemplate = new SimpleJdbcInsert(dataSource).withTableName("orders").usingGeneratedKeyColumns("id");
        this.insertOrderBurritoTemplate = new SimpleJdbcInsert(dataSource).withTableName("order_burritos");
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public Optional<Order> findById(Long id) {
        List<Order> results = template.query(FIND_BY_ID_QUERY, Map.of("id", id), resultSetExtractor);
        return results != null && results.size() > 0 ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return template.query(FIND_ALL_QUERY, resultSetExtractor);
    }

    @Override
    public Order create(Order order) {
        log.info("Create new order: {}", order);
        Map<String, Object> valueMap = orderToValueMap(order);
        long id = (long) insertOrderTemplate.executeAndReturnKey(valueMap);
        for (Burrito burrito : order.getBurritos()) {
            insertOrderBurritoTemplate.execute(Map.of("order_id", id, "burrito_id", burrito.getId()));
        }
        return order.toBuilder().id(id).build();
    }

    @Override
    public boolean update(Order incoming) {
        Long id = incoming.getId();
        Objects.requireNonNull(id, "ID is mandatory");

        Optional<Order> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }


//        Burrito existing = existingOpt.get();
//
//        Map<String, ?> updateParams = Map.of("id", id, "name", incoming.getName());
//        jdbcTemplate.update("UPDATE burrito SET name = :name WHERE id = :id", updateParams);
//
//        List<String> existingIngredients = new ArrayList<>(existing.getIngredients());
//        existingIngredients.sort(String::compareTo);
//
//        List<String> incomingIngredients = new ArrayList<>(incoming.getIngredients());
//        incomingIngredients.sort(String::compareTo);
//        if (!existingIngredients.equals(incomingIngredients)) {
//            jdbcTemplate.update("DELETE FROM burrito_ingredients WHERE burrito_id = :id", Map.of("id", id));
//            incoming.getIngredients().forEach(ing -> linkIngredientToBurrito(id, ing));
//        }
        return true;
    }

    // TODO: implement
    @Override
    public boolean delete(Long id) {
        Optional<Order> byId = findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        Map<String, Long> params = Map.of("id", id);
        template.update("DELETE FROM order_burritos WHERE order_id = :id", params);
        template.update("DELETE FROM orders WHERE id = :id", params);
        return true;
    }


    private Map<String, Object> orderToValueMap(Order order) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", order.getOrderName());
        result.put("street", order.getStreet());
        result.put("town", order.getTown());
        result.put("county", order.getCounty());
        result.put("postcode", order.getPostcode());
        result.put("ccNo", order.getCreditCardNo());
        result.put("ccExpiryDate", order.getCreditCardExpiryDate());
        result.put("ccCCV", order.getCreditCardCCV());
        return result;
    }
}
