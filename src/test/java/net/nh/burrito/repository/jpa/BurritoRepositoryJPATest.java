package net.nh.burrito.repository.jpa;


import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.OrderJPA;
import net.nh.burrito.repository.EntityConverter;
import net.nh.burrito.repository.JPARepositoryTestDataVerifier;
import net.nh.burrito.repository.jdbc.JdbcRepoTestFixture;
import net.nh.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies the key methods of the repository to ensure the entity has been mapped correctly.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BurritoRepositoryJPATest {

    private final BurritoRepositoryJPA repository;
    private final JPARepositoryTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;
    private final EntityConverter converter;

    @Autowired
    public BurritoRepositoryJPATest(BurritoRepositoryJPA repository, JPARepositoryTestDataVerifier verifier,
                                    JdbcRepoTestFixture fixture, EntityConverter converter) {
        this.repository = repository;
        this.verifier = verifier;
        this.fixture = fixture;
        this.converter = converter;
    }

    @BeforeEach
    public void setup() {
        fixture.initialiseData();
    }

    @Test
    public void findById_shouldReturnEmptyOptional_whenNoRecordExists() {
        //given:
        Long invalidId = 90100L;

        //when:
        Optional<BurritoJPA> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    public void findById_shouldReturnValue_whenRecordExists() {
        //given:
        BurritoJPA expected = converter.toBurritoJPA(fixture.chickenBurrito());

        //when:
        Optional<BurritoJPA> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyBurrito(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectValues() {
        //given:
        List<BurritoJPA> burritos = fixture.burritos().stream().map(converter::toBurritoJPA).collect(toList());

        //when:
        List<BurritoJPA> results = Utils.toList(repository.findAll());

        //then:
        verifier.verifyBurritos(burritos, results);
    }

    @Test
    void save_shouldCreateNewEntity() {
        //given:
        BurritoJPA newBurrito = BurritoJPA.builder().name("new-burrito").build();
        newBurrito.getIngredients().add(converter.toIngredientJPA(fixture.chicken()));

        //when:
        BurritoJPA saved = repository.save(newBurrito);

        //then:
        assertNotNull(saved.getId());
        verifier.verifyBurritoWasPersistedCorrectly(saved);
    }

    @Test
    void save_shouldUpdateExistingEntity() {
        //given:
        BurritoJPA updated = converter.toBurritoJPA(fixture.chickenBurrito());
        updated.setName("NameIsUpdated");
        updated.getIngredients().add(converter.toIngredientJPA(fixture.beef()));

        //when:
        repository.save(updated);

        //then:
        verifier.verifyBurritoWasPersistedCorrectly(updated);
    }

    @Test
    void delete_shouldRemoveEntity() {
        //given:
        BurritoJPA toDelete = converter.toBurritoJPA(fixture.unattachedBurrito());

        //when:
        repository.delete(toDelete);

        //then:
        verifier.verifyBurritoDoesNotExist(toDelete.getId());
    }
}
