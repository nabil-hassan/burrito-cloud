package net.nh.burrito.repository;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
import net.nh.burrito.repository.jdbc.JdbcBurritoRepository;
import net.nh.burrito.repository.jdbc.JdbcOrderRepository;
import net.nh.burrito.repository.jdbc.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class JdbcOrderRepositoryTest {

    private final NamedParameterJdbcTemplate namedTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcOrderRepository repository;
    private final OrderMapper orderMapper;
    private final JdbcBurritoRepository burritoRepository;

    @Autowired
    public JdbcOrderRepositoryTest(NamedParameterJdbcTemplate namedTemplate, JdbcTemplate jdbcTemplate, JdbcOrderRepository repository, JdbcBurritoRepository burritoRepository) {
        this.namedTemplate = namedTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
        this.burritoRepository = burritoRepository;
        this.orderMapper = new OrderMapper();
    }

    @BeforeEach
    void setUp() {
        log.info("Clear orders table");
        jdbcTemplate.update("DELETE FROM orders");
    }

    @Test
    void saveOrder_shouldPersistOrderAndBurritoDetails() {
        //GIVEN
        Burrito burrito = burritoRepository.save(Burrito.builder().name("b1").build());
        Long burritoId = burrito.getId();
        Order order = Order.builder().name("o1").street("s1").town("t1").county("c1").postcode("p1").creditCardNo("ccn1")
                .creditCardExpiryDate("ced1").creditCardCCV("cv1").burritos(List.of(burrito)).build();

        //WHEN
        Order savedOrder = repository.save(order);
        Long orderId = savedOrder.getId();

        //THEN
        List<Order> storedOrders = namedTemplate.query("SELECT * FROM orders WHERE id = :id", Map.of("id", orderId), orderMapper);
        assertEquals(1, storedOrders.size());
        verifyOrder(orderId, order, storedOrders.get(0));
        Long burritoOrdersCount = namedTemplate.query("SELECT count(*) as theCount FROM order_burritos WHERE order_id = :orderId AND burrito_id = :burritoId",
                Map.of("orderId", orderId, "burritoId", burritoId), rs -> {
                    rs.next();
                    return rs.getLong("theCount");
                });
        assertEquals(1, burritoOrdersCount);
    }

    private void verifyOrder(Long orderId, Order expected, Order actual) {
        assertEquals(orderId, actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStreet(), expected.getStreet());
        assertEquals(expected.getTown(), actual.getTown());
        assertEquals(expected.getCounty(), expected.getCounty());
        assertEquals(expected.getPostcode(), expected.getPostcode());
        assertEquals(expected.getCreditCardNo(), expected.getCreditCardNo());
        assertEquals(expected.getCreditCardExpiryDate(), expected.getCreditCardExpiryDate());
        assertEquals(expected.getCreditCardCCV(), expected.getCreditCardCCV());

    }
}