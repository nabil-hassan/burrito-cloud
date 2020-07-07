package net.nh.burrito.repository.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Ingredient;
import net.nh.burrito.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.nh.burrito.entity.Ingredient.Type.MEAT;
import static net.nh.burrito.entity.Ingredient.Type.SAUCE;
import static net.nh.burrito.entity.Ingredient.Type.VEGETABLE;

/**
 * Used to setup example data for the repository tests.
 */
@Component
public class JdbcRepoTestFixture {

    private final SimpleJdbcInsert insertBurritoJdbc, insertBurritoIngredientsJdbc, insertIngredientJdbc, insertOrderJdbc, insertOrderBurritosJdbc;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private Ingredient CHICKEN, BEEF, LETTUCE, SALSA;
    private Burrito CHICKEN_BURRITO, BEEF_LETTUCE_BURRITO;
    private Order CHICKEN_BURRITO_ORDER, BEEF_BURRITO_ORDER;

    @Autowired
    public JdbcRepoTestFixture(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.insertBurritoJdbc = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("burrito").usingGeneratedKeyColumns("id");
        this.insertBurritoIngredientsJdbc = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("burrito_ingredients");
        this.insertIngredientJdbc = new SimpleJdbcInsert(this.jdbcTemplate).withTableName("ingredient");
        this.insertOrderJdbc = new SimpleJdbcInsert(jdbcTemplate).withTableName("orders").usingGeneratedKeyColumns("id");
        this.insertOrderBurritosJdbc = new SimpleJdbcInsert(jdbcTemplate).withTableName("order_burritos");
    }

    public void initialiseData() {
        jdbcTemplate.update("DELETE FROM burrito_ingredients");
        jdbcTemplate.update("DELETE FROM ingredient");
        jdbcTemplate.update("DELETE FROM order_burritos");
        jdbcTemplate.update("DELETE FROM burrito");
        jdbcTemplate.update("DELETE FROM orders");

        //================================================ Ingredients ======================================
        CHICKEN = Ingredient.builder().id("CHCK").name("Chicken").type(MEAT).build();
        insertIngredientJdbc.execute(objectMapper.convertValue(CHICKEN, Map.class));

        BEEF = Ingredient.builder().id("BEEF").name("Beef").type(MEAT).build();
        insertIngredientJdbc.execute(objectMapper.convertValue(BEEF, Map.class));

        LETTUCE = Ingredient.builder().id("LTTE").name("Lettuce").type(VEGETABLE).build();
        insertIngredientJdbc.execute(objectMapper.convertValue(LETTUCE, Map.class));

        SALSA = Ingredient.builder().id("SLSA").name("Salsa").type(SAUCE).build();
        insertIngredientJdbc.execute(objectMapper.convertValue(SALSA, Map.class));

        //================================================ BURRITOS ======================================
        List<String> chickenBurritoIngredients = new ArrayList<>();
        chickenBurritoIngredients.add(CHICKEN.getId());
        CHICKEN_BURRITO = Burrito.builder().name("chicken_burrito").ingredients(chickenBurritoIngredients).build();
        long chickenBurritoId  = (long) insertBurritoJdbc.executeAndReturnKey(Map.of("name", CHICKEN_BURRITO.getName(), "created_at", new Timestamp(new Date().getTime())));
        CHICKEN_BURRITO.setId(chickenBurritoId);
        insertBurritoIngredientsJdbc.execute(Map.of("burrito_id", chickenBurritoId, "ingredient_id", CHICKEN.getId()));

        List<String> beefBurritoIngredients = new ArrayList<>();
        beefBurritoIngredients.add(BEEF.getId());
        beefBurritoIngredients.add(LETTUCE.getId());
        BEEF_LETTUCE_BURRITO = Burrito.builder().name("beef_burrito").build();
        BEEF_LETTUCE_BURRITO.setIngredients(beefBurritoIngredients);
        long beefBurritoId  = (long) insertBurritoJdbc.executeAndReturnKey(Map.of("name", BEEF_LETTUCE_BURRITO.getName(), "created_at", new Timestamp(new Date().getTime())));
        BEEF_LETTUCE_BURRITO.setId(beefBurritoId);
        insertBurritoIngredientsJdbc.execute(Map.of("burrito_id", beefBurritoId, "ingredient_id", BEEF.getId()));
        insertBurritoIngredientsJdbc.execute(Map.of("burrito_id", beefBurritoId, "ingredient_id", LETTUCE.getId()));

        //================================================ ORDERS ======================================
        List<Burrito> chickenOrderBurritos = new ArrayList<>();
        chickenOrderBurritos.add(CHICKEN_BURRITO);
        CHICKEN_BURRITO_ORDER = Order.builder().orderName("chicken_order").street("street1").town("town1").county("county1").postcode("postcode1").creditCardNo("12345678")
                .creditCardExpiryDate("0304").creditCardCCV("670").burritos(chickenOrderBurritos).build();
        long chickenOrderId = (long) insertOrderJdbc.executeAndReturnKey(orderMap(CHICKEN_BURRITO_ORDER));
        CHICKEN_BURRITO_ORDER.setId(chickenOrderId);
        insertOrderBurritosJdbc.execute(Map.of("burrito_id", CHICKEN_BURRITO.getId(),"order_id", CHICKEN_BURRITO_ORDER.getId()));

        List<Burrito> beefOrderBurritos = new ArrayList<>();
        beefOrderBurritos.add(BEEF_LETTUCE_BURRITO);
        BEEF_BURRITO_ORDER = Order.builder().orderName("beef_order").street("street2").town("town2").county("county2").postcode("postcode2").creditCardNo("87654321")
                .creditCardExpiryDate("0405").creditCardCCV("980").burritos(beefOrderBurritos).build();
        long beefOrderId = (long) insertOrderJdbc.executeAndReturnKey(orderMap(BEEF_BURRITO_ORDER));
        BEEF_BURRITO_ORDER.setId(beefOrderId);
        insertOrderBurritosJdbc.execute(Map.of("burrito_id", BEEF_LETTUCE_BURRITO.getId(),"order_id", BEEF_BURRITO_ORDER.getId()));
    }

    public Ingredient chicken() {
        return CHICKEN;
    }

    public Ingredient beef() {
        return BEEF;
    }

    public Ingredient lettuce() {
        return LETTUCE;
    }

    public Ingredient salsa() {
        return SALSA;
    }

    public Burrito chickenBurrito() {
        return CHICKEN_BURRITO;
    }

    public Burrito beefLettuceBurrito() {
        return BEEF_LETTUCE_BURRITO;
    }

    public Order chickenOrder() {
        return CHICKEN_BURRITO_ORDER;
    }

    public Order beefOrder() {
        return BEEF_BURRITO_ORDER;
    }

    public List<Order> orders() {
        List<Order> results = new ArrayList<>();
        results.add(CHICKEN_BURRITO_ORDER);
        results.add(BEEF_BURRITO_ORDER);
        return results;
    }

    public List<Ingredient> ingredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(BEEF);
        ingredients.add(CHICKEN);
        ingredients.add(LETTUCE);
        ingredients.add(SALSA);
        return ingredients;
    }

    public List<Burrito> burritos() {
        List<Burrito> burritos = new ArrayList<>();
        burritos.add(BEEF_LETTUCE_BURRITO);
        burritos.add(CHICKEN_BURRITO);
        return burritos;
    }

    private Map<String, ?> orderMap(Order order) {
        Map<String, String> result = new HashMap<>();
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
