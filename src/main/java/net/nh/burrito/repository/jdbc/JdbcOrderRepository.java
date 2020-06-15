package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
import net.nh.burrito.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert insertOrderTemplate;
    private final SimpleJdbcInsert insertOrderBurritoTemplate;

    @Autowired
    public JdbcOrderRepository(NamedParameterJdbcTemplate template, DataSource dataSource) {
        this.template = template;
        this.insertOrderTemplate = new SimpleJdbcInsert(dataSource).withTableName("orders").usingGeneratedKeyColumns("id");
        this.insertOrderBurritoTemplate = new SimpleJdbcInsert(dataSource).withTableName("order_burritos");
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

    //TODO: implement
    //TODO: test
    @Override
    public boolean update(Order order) {
        return false;
    }

    //TODO: implement
    //TODO: test
    @Override
    public List<Order> findAll() {
        return null;
    }

    //TODO: implement
    //TODO: test
    @Override
    public Optional<Order> findById() {
        return Optional.empty();
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
