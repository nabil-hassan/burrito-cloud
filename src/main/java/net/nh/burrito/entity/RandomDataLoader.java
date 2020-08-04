package net.nh.burrito.entity;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.repository.jpa.BurritoRepositoryJPA;
import net.nh.burrito.repository.jpa.IngredientRepositoryJPA;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Loads some initial burritos and orders into the system. <p>
 * n.b. ingredients are loaded by resources/data.sql as they are more straightforward.
 */
@Component
@Slf4j
public class RandomDataLoader implements InitializingBean {

    private final BurritoRepositoryJPA burritoRepository;
    private final IngredientRepositoryJPA ingredientRepository;

    @Autowired
    public RandomDataLoader(BurritoRepositoryJPA burritoRepository, IngredientRepositoryJPA ingredientRepository) {
        this.burritoRepository = burritoRepository;
        this.ingredientRepository = ingredientRepository;
    }


    @Override
    public void afterPropertiesSet() {
        List<IngredientJPA> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);
        int upperBoundIngredients = ingredients.size();

        // Load 100 burritos - each with a randomly chosen ingredient
        for (int i = 0; i < 100; i++) {
            int ingredientIndex = ThreadLocalRandom.current().nextInt(0, upperBoundIngredients - 1);
            IngredientJPA ingredient = ingredients.get(ingredientIndex);
            BurritoJPA burrito = BurritoJPA.builder().name("Burrito" + i).ingredients(List.of(ingredient)).build();
            burritoRepository.save(burrito);
        }
        log.info("Loaded initial burrito set");
    }
}
