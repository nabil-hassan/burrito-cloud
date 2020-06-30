package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@Component
public class JdbcRepoTestDataVerifier {

    private NamedParameterJdbcTemplate namedParamTemplate;

    @Autowired
    public JdbcRepoTestDataVerifier(NamedParameterJdbcTemplate namedParamTemplate) {
        this.namedParamTemplate = namedParamTemplate;
    }

    public void verifyBurrito(Burrito expected, Burrito actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getIngredients().size(), actual.getIngredients().size());

        List<String> expectedIngredients = new ArrayList<>(expected.getIngredients());
        List<String> actualIngredients = new ArrayList<>(actual.getIngredients());

        expectedIngredients.sort(String::compareTo);
        actualIngredients.sort(String::compareTo);

        assertEquals(expected.getIngredients(), actual.getIngredients());
    }

    public void verifyBurritos(List<Burrito> expectedResults, List<Burrito> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<Burrito> byName = Comparator.comparing(Burrito::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);

        for (int i = 0; i < expectedResults.size(); i++) {
            log.info("Comparing burrito result index: {}", i);
            verifyBurrito(expectedResults.get(i), actualResults.get(i));
        }
    }

    public void verifyBurritoDoesNotExist(Long id) {
        Long count = namedParamTemplate.query("SELECT count(*) as the_count FROM burrito WHERE id = :id", Map.of("id", id), (rs, i) -> rs.getLong("the_count")).get(0);
        assertEquals(0, count);
    }

    public void verifyBurritoWasPersistedCorrectly(Burrito expected) {
        Burrito stored = new Burrito();
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

    public void verifyIngredient(Ingredient expected, Ingredient actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
    }

    public void verifyIngredients(List<Ingredient> expectedResults, List<Ingredient> actualResults) {
        assertEquals(expectedResults.size(), actualResults.size());
        Comparator<Ingredient> byName = Comparator.comparing(Ingredient::getName);
        expectedResults.sort(byName);
        actualResults.sort(byName);

        for (int i = 0; i < expectedResults.size(); i++) {
            log.info("Comparing ingredient result index: {}", i);
            verifyIngredient(expectedResults.get(i), actualResults.get(i));
        }
    }
}
