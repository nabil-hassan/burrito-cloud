package net.nh.burrito.repository.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
import net.nh.burrito.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public Order save(Order order) {
        log.info("Create new order: {}", order);
        Map<String, Object> valueMap = orderToValueMap(order);
        long id = (long) insertOrderTemplate.executeAndReturnKey(valueMap);
        for (Burrito burrito : order.getBurritos()) {
            insertOrderBurritoTemplate.execute(Map.of("order_id", id, "burrito_id", burrito.getId()));
        }
        return order.toBuilder().id(id).build();
    }

    private Map<String, Object> orderToValueMap(Order order) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", order.getName());
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
