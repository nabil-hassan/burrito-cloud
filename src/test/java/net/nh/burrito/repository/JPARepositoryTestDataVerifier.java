package net.nh.burrito.repository;

import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.entity.jpa.OrderJPA;
import net.nh.burrito.translation.EntityConverter;
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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Component
public class JPARepositoryTestDataVerifier {

    private final NamedParameterJdbcTemplate namedParamTemplate;
    private final EntityConverter converter;

    @Autowired
    public JPARepositoryTestDataVerifier(NamedParameterJdbcTemplate namedParamTemplate, EntityConverter converter) {
        this.namedParamTemplate = namedParamTemplate;
        this.converter = converter;
    }

    public void verifyBurrito(BurritoJPA expected, BurritoJPA actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getIngredients().size(), actual.getIngredients().size());

        List<String> expectedIngredients = expected.getIngredients().stream().map(IngredientJPA::getId).collect(toList());
        List<String> actualIngredients = actual.getIngredients().stream().map(IngredientJPA::getId).collect(toList());

        expectedIngredients.sort(String::compareTo);
        actualIngredients.sort(String::compareTo);

        assertEquals(expectedIngredients, actualIngredients);
    }

    public void verifyBurritos(List<BurritoJPA> expectedResults, List<BurritoJPA> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<BurritoJPA> byName = Comparator.comparing(BurritoJPA::getName);
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

    public void verifyBurritoWasPersistedCorrectly(BurritoJPA expected) {
        BurritoJPA stored = new BurritoJPA();
        stored.setIngredients(new ArrayList<>());
        namedParamTemplate.query("SELECT id, name FROM burrito WHERE id = :id", Map.of("id", expected.getId()), rs -> {
            stored.setId(rs.getLong("id"));
            stored.setName(rs.getString("name"));
        });
        if (stored.getId() == null) {
            fail("No such burrito: " + expected.getId());
        }
        namedParamTemplate.query("SELECT ingredient_id FROM burrito_ingredients WHERE burrito_id = :id", Map.of("id", expected.getId()), rs -> {
            stored.getIngredients().add(IngredientJPA.builder().id(rs.getString("ingredient_id")).build());
        });
        verifyBurrito(expected, stored);
    }

    public void verifyIngredient(IngredientJPA expected, IngredientJPA actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
    }

    public void verifyIngredients(List<IngredientJPA> expectedResults, List<IngredientJPA> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<IngredientJPA> byName = Comparator.comparing(IngredientJPA::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);

        for (int i = 0; i < expectedResults.size(); i++) {
            verifyIngredient(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyOrder(OrderJPA expected, OrderJPA actual) {
        verifyBaseOrderProperties(expected, actual);
        verifyBurritos(expected.getBurritos(), actual.getBurritos());
    }



    private void verifyBaseOrderProperties(OrderJPA expected, OrderJPA actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStreet(), actual.getStreet());
        assertEquals(expected.getTown(), actual.getTown());
        assertEquals(expected.getCounty(), actual.getCounty());
        assertEquals(expected.getPostcode(), actual.getPostcode());
        assertEquals(expected.getCreditCardNo(), actual.getCreditCardNo());
        assertEquals(expected.getCreditCardExpiryDate(), actual.getCreditCardExpiryDate());
        assertEquals(expected.getCreditCardCCV(), actual.getCreditCardCCV());
    }

    public void verifyOrders(List<OrderJPA> expectedResults, List<OrderJPA> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<OrderJPA> byName = Comparator.comparing(OrderJPA::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);
        for (int i = 0; i < expectedResults.size(); i++) {
            verifyOrder(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyOrderWasPersistedCorrectly(OrderJPA expected) {
        OrderJPA actualBaseProperties = namedParamTemplate.query("SELECT * FROM orders WHERE id = :id", Map.of("id", expected.getId()), new BasePropertiesOrderRowMapper()).get(0);
        verifyBaseOrderProperties(expected, actualBaseProperties);

        List<Long> expectedBurritoIds = expected.getBurritos().stream().map(BurritoJPA::getId).sorted(Long::compareTo).collect(toList());
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

    public static class BasePropertiesOrderRowMapper implements RowMapper<OrderJPA> {
        @Override
        public OrderJPA mapRow(ResultSet rs, int rowNum) throws SQLException {
            return OrderJPA.builder().id(rs.getLong("id")).name(rs.getString("name"))
                    .street(rs.getString("street")).town(rs.getString("town")).county(rs.getString("county")).postcode(rs.getString("postcode"))
                    .creditCardCCV(rs.getString("ccccv")).creditCardNo(rs.getString("ccno")).creditCardExpiryDate(rs.getString("ccexpirydate"))
                    .burritos(new ArrayList<>()).build();
        }
    }
}
