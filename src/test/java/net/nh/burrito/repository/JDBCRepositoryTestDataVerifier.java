package net.nh.burrito.repository;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Component
public class JDBCRepositoryTestDataVerifier {

    private NamedParameterJdbcTemplate namedParamTemplate;

    @Autowired
    public JDBCRepositoryTestDataVerifier(NamedParameterJdbcTemplate namedParamTemplate) {
        this.namedParamTemplate = namedParamTemplate;
    }

    public void verifyBurrito(BurritoJDBC expected, BurritoJDBC actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getIngredients().size(), actual.getIngredients().size());

        List<String> expectedIngredients = new ArrayList<>(expected.getIngredients());
        List<String> actualIngredients = new ArrayList<>(actual.getIngredients());

        expectedIngredients.sort(String::compareTo);
        actualIngredients.sort(String::compareTo);

        assertEquals(expected.getIngredients(), actual.getIngredients());
    }

    public void verifyBurritos(List<BurritoJDBC> expectedResults, List<BurritoJDBC> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<BurritoJDBC> byName = Comparator.comparing(BurritoJDBC::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);

        for (int i = 0; i < expectedResults.size(); i++) {
            verifyBurrito(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyBurritoDoesNotExist(Long id) {
        Long countBase = namedParamTemplate.query("SELECT count(*) as the_count FROM burrito WHERE id = :id", Map.of("id", id), (rs, i) -> rs.getLong("the_count")).get(0);
        assertEquals(0, countBase);
        Long countLinks = namedParamTemplate.query("SELECT count(*) as the_count FROM burrito_ingredients WHERE burrito_id = :id", Map.of("id", id), (rs, i) -> rs.getLong("the_count")).get(0);
        assertEquals(0, countLinks);
    }

    public void verifyBurritoWasPersistedCorrectly(BurritoJDBC expected) {
        BurritoJDBC stored = new BurritoJDBC();
        stored.setIngredients(new ArrayList<>());
        namedParamTemplate.query("SELECT id, name FROM burrito WHERE id = :id", Map.of("id", expected.getId()), rs -> {
            stored.setId(rs.getLong("id"));
            stored.setName(rs.getString("name"));
        });
        if (stored.getId() == null) {
            fail("No such burrito: " + expected.getId());
        }
        namedParamTemplate.query("SELECT ingredient_id FROM burrito_ingredients WHERE burrito_id = :id", Map.of("id", expected.getId()), rs -> {
            stored.getIngredients().add(rs.getString("ingredient_id"));
        });
        verifyBurrito(expected, stored);
    }

    public void verifyIngredient(IngredientJDBC expected, IngredientJDBC actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
    }

    public void verifyIngredients(List<IngredientJDBC> expectedResults, List<IngredientJDBC> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<IngredientJDBC> byName = Comparator.comparing(IngredientJDBC::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);

        for (int i = 0; i < expectedResults.size(); i++) {
            verifyIngredient(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyOrder(OrderJDBC expected, OrderJDBC actual) {
        verifyBaseOrderProperties(expected, actual);
        verifyBurritos(expected.getBurritos(), actual.getBurritos());
    }

    private void verifyBaseOrderProperties(OrderJDBC expected, OrderJDBC actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getOrderName(), actual.getOrderName());
        assertEquals(expected.getStreet(), actual.getStreet());
        assertEquals(expected.getTown(), actual.getTown());
        assertEquals(expected.getCounty(), actual.getCounty());
        assertEquals(expected.getPostcode(), actual.getPostcode());
        assertEquals(expected.getCreditCardNo(), actual.getCreditCardNo());
        assertEquals(expected.getCreditCardExpiryDate(), actual.getCreditCardExpiryDate());
        assertEquals(expected.getCreditCardCCV(), actual.getCreditCardCCV());
    }

    public void verifyOrders(List<OrderJDBC> expectedResults, List<OrderJDBC> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<OrderJDBC> byName = Comparator.comparing(OrderJDBC::getOrderName);
        expectedResults.sort(byName);
        actualResults.sort(byName);
        for (int i = 0; i < expectedResults.size(); i++) {
            verifyOrder(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyOrderWasPersistedCorrectly(OrderJDBC expected) {
        OrderJDBC actualBaseProperties = namedParamTemplate.query("SELECT * FROM orders WHERE id = :id", Map.of("id", expected.getId()), new BasePropertiesOrderRowMapper()).get(0);
        verifyBaseOrderProperties(expected, actualBaseProperties);

        List<Long> expectedBurritoIds = expected.getBurritos().stream().map(BurritoJDBC::getId).sorted(Long::compareTo).collect(Collectors.toList());
        List<Long> actualBurritoIds = namedParamTemplate.query("SELECT * FROM order_burritos WHERE order_id = :id", Map.of("id", expected.getId()), (rs, i) -> rs.getLong("burrito_id"));
        actualBurritoIds.sort(Long::compareTo);
        assertEquals(expectedBurritoIds, actualBurritoIds);
    }

    public void verifyOrderDoesNotExist(Long id) {
        Long countBase = namedParamTemplate.query("SELECT count(*) as the_count FROM orders WHERE id = :id", Map.of("id", id), (rs, i) -> rs.getLong("the_count")).get(0);
        assertEquals(0, countBase);
        Long countLinks = namedParamTemplate.query("SELECT count(*) as the_count FROM order_burritos WHERE order_id = :id", Map.of("id", id), (rs, i) -> rs.getLong("the_count")).get(0);
        assertEquals(0, countLinks);
    }

    public static class BasePropertiesOrderRowMapper implements RowMapper<OrderJDBC> {
        @Override
        public OrderJDBC mapRow(ResultSet rs, int rowNum) throws SQLException {
            return OrderJDBC.builder().id(rs.getLong("id")).orderName(rs.getString("name"))
                    .street(rs.getString("street")).town(rs.getString("town")).county(rs.getString("county")).postcode(rs.getString("postcode"))
                    .creditCardCCV(rs.getString("ccccv")).creditCardNo(rs.getString("ccno")).creditCardExpiryDate(rs.getString("ccexpirydate"))
                    .burritos(new ArrayList<>()).build();
        }
    }
}
