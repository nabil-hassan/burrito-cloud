package net.nh.burrito.repository.jpa;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static net.nh.burrito.entity.IngredientType.MEAT;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class BurritoPageableRepositoryJPATest {

    private final BurritoPageableRepositoryJPA repository;
    private final IngredientRepositoryJPA ingredientRepository;
    private final IngredientJPA ingredient = IngredientJPA.builder().id("BEEF").name("beef").type(MEAT).build();

    @Autowired
    public BurritoPageableRepositoryJPATest(BurritoPageableRepositoryJPA repository, IngredientRepositoryJPA ingredientRepository) {
        this.repository = repository;
        this.ingredientRepository = ingredientRepository;
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        ingredientRepository.save(ingredient);
    }

    @Test
    void findAllPaged() {
        // Create 40 burritos with the same name
        for (int i = 0; i < 40; i++) {
            repository.save(BurritoJPA.builder().name("burrito_" + i).ingredients(List.of(ingredient)).build());
        }

        // Fetch the first page to inform further fetches
        Slice<BurritoJPA> results = repository.findAllByNameLike("burrito%", PageRequest.of(0, 10, Sort.by("name")));
        if (!results.hasContent()) {
            throw new RuntimeException("No data found!");
        }
        // Fetch subsequent pages
        while(results.hasNext()) {
            Pageable page = results.nextPageable();
            results = repository.findAllByNameLike("burrito%", page);
            List<String> names = results.map(BurritoJPA::getName).getContent();
            log.info("Process page {}. Data is: {}", page.getPageNumber(), names);
        }
    }

    @Test
    void findAllByNamePageable() {
        // Create 40 burritos with the same name
        for (int i = 0; i < 20; i++) {
            repository.save(BurritoJPA.builder().name("the_name").build());
        }
//        repository.findAllByName("the_name");
    }
}
