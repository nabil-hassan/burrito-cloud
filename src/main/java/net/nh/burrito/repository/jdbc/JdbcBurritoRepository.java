package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.repository.BurritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Slf4j
@Repository
public class JdbcBurritoRepository implements BurritoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public JdbcBurritoRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("burrito").usingGeneratedKeyColumns("id");
    }

    @Override
    public Burrito save(Burrito burrito) {
        log.info("Saving burrito: {}", burrito);
        long id  = (long) simpleJdbcInsert.executeAndReturnKey(Map.of("name", burrito.getName(), "created_at", new Timestamp(new Date().getTime())));
        log.debug("Created burrito with id: {}", id);

        burrito.getIngredients().forEach(i -> linkIngredientToBurrito(id, i));

        log.info("Successfully saved burrito: {}", id);
        return burrito.toBuilder().id(id).build();
    }

    private void linkIngredientToBurrito(long burritoId, String ingredientId) {
        log.debug("Linking ingredient: {} to burrito: {}", ingredientId, burritoId);
        jdbcTemplate.update("insert into burrito_ingredients(burrito_id, ingredient_id) values(:burritoId, :ingredientId)",
                new MapSqlParameterSource().addValue("burritoId", burritoId).addValue("ingredientId", ingredientId));
    }
}