package net.nh.burrito.repository.jpa;

import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.translation.EntityConverter;
import net.nh.burrito.repository.JPARepositoryTestDataVerifier;
import net.nh.burrito.repository.jdbc.JdbcRepoTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IngredientRepositoryJPATest {

    private final IngredientRepositoryJPA repository;
    private final EntityConverter converter;
    private final JPARepositoryTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;

    @Autowired
    IngredientRepositoryJPATest(IngredientRepositoryJPA repository, EntityConverter converter, JPARepositoryTestDataVerifier verifier, JdbcRepoTestFixture fixture) {
        this.repository = repository;
        this.converter = converter;
        this.verifier = verifier;
        this.fixture = fixture;
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
        Optional<IngredientJPA> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectIngredient_whenIngredientExists() {
        //given:
        IngredientJPA expected = converter.toIngredientJPA(fixture.lettuce());

        //when:
        Optional<IngredientJPA> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyIngredient(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnAllPersistedIngredients() {
        //given:
        List<IngredientJPA> expected = fixture.ingredients().stream().map(converter::toIngredientJPA).collect(toList());

        //when:
        List<IngredientJPA> results = new ArrayList<>();
        repository.findAll().forEach(results::add);

        //then:
        verifier.verifyIngredients(expected, results);
    }

}