package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.repository.JDBCRepositoryTestDataVerifier;
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
public class IngredientRepositoryJDBCTest {

    private final IngredientRepositoryJDBC repository;
    private final JdbcRepoTestFixture fixture;
    private final JDBCRepositoryTestDataVerifier dataVerifier;

    @Autowired
    public IngredientRepositoryJDBCTest(IngredientRepositoryJDBC repository, JdbcRepoTestFixture fixture,
                                        JDBCRepositoryTestDataVerifier dataVerifier) {
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
        Optional<IngredientJDBC> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectIngredient_whenIngredientExists() {
        //given:
        IngredientJDBC expected = fixture.lettuce();

        //when:
        Optional<IngredientJDBC> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        dataVerifier.verifyIngredient(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnAllPersistedIngredients() {
        //given:
        List<IngredientJDBC> expected = fixture.ingredients();

        //when:
        List<IngredientJDBC> results = repository.findAll();

        //then:
        dataVerifier.verifyIngredients(expected, results);
    }

}