package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
import net.nh.burrito.repository.OrderRepository;
import net.nh.burrito.repository.jdbc.translation.OrderResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OrderRepositoryJDBC implements OrderRepository {

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
    public OrderRepositoryJDBC(NamedParameterJdbcTemplate template, DataSource dataSource, OrderResultSetExtractor resultSetExtractor) {
        this.template = template;
        this.insertOrderTemplate = new SimpleJdbcInsert(dataSource).withTableName("orders").usingGeneratedKeyColumns("id");
        this.insertOrderBurritoTemplate = new SimpleJdbcInsert(dataSource).withTableName("order_burritos");
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public Optional<OrderJDBC> findById(Long id) {
        List<OrderJDBC> results = template.query(FIND_BY_ID_QUERY, Map.of("id", id), resultSetExtractor);
        return results != null && results.size() > 0 ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public List<OrderJDBC> findAll() {
        return template.query(FIND_ALL_QUERY, resultSetExtractor);
    }

    @Override
    public OrderJDBC create(OrderJDBC order) {
        log.info("Create new order: {}", order);
        Map<String, Object> valueMap = orderToValueMap(order);
        long id = (long) insertOrderTemplate.executeAndReturnKey(valueMap);
        for (BurritoJDBC burrito : order.getBurritos()) {
            insertOrderBurritoTemplate.execute(Map.of("order_id", id, "burrito_id", burrito.getId()));
        }
        return order.toBuilder().id(id).build();
    }

    @Override
    public boolean update(OrderJDBC incoming) {
        Long id = incoming.getId();
        Objects.requireNonNull(id, "ID is mandatory");
        Optional<OrderJDBC> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("id", incoming.getId());
        updateParams.put("name", incoming.getOrderName());
        updateParams.put("street", incoming.getStreet());
        updateParams.put("town", incoming.getTown());
        updateParams.put("county", incoming.getCounty());
        updateParams.put("postcode", incoming.getPostcode());
        updateParams.put("ccNo", incoming.getCreditCardNo());
        updateParams.put("ccExpiryDate", incoming.getCreditCardExpiryDate());
        updateParams.put("ccCCV", incoming.getCreditCardCCV());
        template.update("UPDATE orders SET name = :name, street = :street, town = :town, county = :county, postcode = :postcode, " +
                "ccNo = :ccNo, ccExpiryDate = :ccExpiryDate, ccCCV = :ccCCV WHERE id = :id", updateParams);

        OrderJDBC existing = existingOpt.get();
        List<Long> existingBurritoIds = existing.getBurritos().stream().map(BurritoJDBC::getId).sorted().collect(Collectors.toList());
        List<Long> newBurritoIds = incoming.getBurritos().stream().map(BurritoJDBC::getId).sorted().collect(Collectors.toList());
        if (!existingBurritoIds.equals(newBurritoIds)) {
            template.update("DELETE FROM order_burritos WHERE order_id = :id", Map.of("id", id));
            newBurritoIds.forEach(burId -> {
                template.update("INSERT INTO order_burritos(order_id, burrito_id) VALUES(:orderId, :burritoId)", Map.of("orderId", incoming.getId(), "burritoId", burId));
            });
        }
        return true;
    }

    @Override
    public boolean delete(Long id) {
        Optional<OrderJDBC> byId = findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        Map<String, Long> params = Map.of("id", id);
        template.update("DELETE FROM order_burritos WHERE order_id = :id", params);
        template.update("DELETE FROM orders WHERE id = :id", params);
        return true;
    }


    private Map<String, Object> orderToValueMap(OrderJDBC order) {
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
