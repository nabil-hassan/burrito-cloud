package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class JdbcIngredientRepositoryTest {

    private final JdbcIngredientRepository repository;
    private final JdbcRepoTestFixture fixture;
    private final JdbcRepoTestDataVerifier dataVerifier;

    @Autowired
    public JdbcIngredientRepositoryTest(JdbcIngredientRepository repository, JdbcRepoTestFixture fixture,
                                        JdbcRepoTestDataVerifier dataVerifier) {
        this.repository = repository;
        this.fixture = fixture;
        this.dataVerifier = dataVerifier;
    }

    @BeforeEach
    void setup() {
        fixture.initialiseData();
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenIngredientDoesNotExist() {
        //given:
        String invalidId = "THIS_NOT_EXIST";

        //when:
        Optional<Ingredient> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectIngredient_whenIngredientExists() {
        //given:
        Ingredient expected = fixture.lettuce();

        //when:
        Optional<Ingredient> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        dataVerifier.verifyIngredient(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnAllPersistedIngredients() {
        //given:
        List<Ingredient> expected = fixture.ingredients();

        //when:
        List<Ingredient> results = repository.findAll();

        //then:
        dataVerifier.verifyIngredients(expected, results);
    }

}