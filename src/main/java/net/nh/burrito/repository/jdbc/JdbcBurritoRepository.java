package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.repository.BurritoRepository;
import net.nh.burrito.repository.jdbc.translation.BurritoResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcBurritoRepository implements BurritoRepository {

    private static final String FIND_ALL_QUERY = "SELECT b.id as burrito_id, b.name as burrito_name, " +
            "i.id as ingredient_id, i.name as ingredient_name, i.type as ingredient_type\n" +
            "FROM burrito b\n" +
            "LEFT OUTER JOIN burrito_ingredients bi ON b.id = bi.burrito_id\n" +
            "LEFT OUTER JOIN ingredient i ON i.id = bi.ingredient_id";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + "\n" + "WHERE b.id = :burritoId";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final BurritoResultSetExtractor rsExtractor;

    @Autowired
    public JdbcBurritoRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource, BurritoResultSetExtractor rsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("burrito").usingGeneratedKeyColumns("id");
        this.rsExtractor = rsExtractor;
    }

    @Override
    public List<Burrito> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, rsExtractor);
    }

    @Override
    public Optional<Burrito> findById(Long id) {
        List<Burrito> results = jdbcTemplate.query(FIND_BY_ID_QUERY, Map.of("burritoId", id), rsExtractor);
        return results != null && results.size() > 0 ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public Burrito create(Burrito burrito) {
        log.info("Saving burrito: {}", burrito);
        long id  = (long) simpleJdbcInsert.executeAndReturnKey(Map.of("name", burrito.getName(), "created_at", new Timestamp(new Date().getTime())));
        log.debug("Created burrito with id: {}", id);

        burrito.getIngredients().forEach(i -> linkIngredientToBurrito(id, i));

        log.info("Successfully saved burrito: {}", id);
        return burrito.toBuilder().id(id).build();
    }

    @Override
    public boolean update(Burrito incoming) {
        Long id = incoming.getId();
        Objects.requireNonNull(id, "ID is mandatory");

        Optional<Burrito> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }
        Burrito existing = existingOpt.get();

        Map<String, ?> updateParams = Map.of("id", id, "name", incoming.getName());
        jdbcTemplate.update("UPDATE burrito SET name = :name WHERE id = :id", updateParams);

        List<String> existingIngredients = new ArrayList<>(existing.getIngredients());
        existingIngredients.sort(String::compareTo);

        List<String> incomingIngredients = new ArrayList<>(incoming.getIngredients());
        incomingIngredients.sort(String::compareTo);
        if (!existingIngredients.equals(incomingIngredients)) {
            jdbcTemplate.update("DELETE FROM burrito_ingredients WHERE burrito_id = :id", Map.of("id", id));
            incoming.getIngredients().forEach(ing -> linkIngredientToBurrito(id, ing));
        }
        return true;
    }

    @Override
    public boolean delete(Long id) {
        Optional<Burrito> byId = findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        Map<String, Long> params = Map.of("id", id);
        jdbcTemplate.update("DELETE FROM burrito_ingredients WHERE burrito_id = :id", params);
        jdbcTemplate.update("DELETE FROM burrito WHERE id = :id", params);
        return true;
    }

    private void linkIngredientToBurrito(long burritoId, String ingredientId) {
        log.debug("Linking ingredient: {} to burrito: {}", ingredientId, burritoId);
        jdbcTemplate.update("insert into burrito_ingredients(burrito_id, ingredient_id) values(:burritoId, :ingredientId)",
                new MapSqlParameterSource().addValue("burritoId", burritoId).addValue("ingredientId", ingredientId));
    }
}
