package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class JdbcBurritoRepositoryTest {

    private final JdbcBurritoRepository repository;
    private final JdbcRepoTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;

    @Autowired
    public JdbcBurritoRepositoryTest(JdbcBurritoRepository repository, JdbcRepoTestDataVerifier verifier,
                                     JdbcRepoTestFixture fixture) {
        this.repository = repository;
        this.verifier = verifier;
        this.fixture = fixture;
    }

    @BeforeEach
    void setUp() {
        fixture.initialiseData();
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNoRowExists() {
        //given:
        long invalidId = 7391l;

        //when:
        Optional<Burrito> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectBurrito_whenRowExists() {
        //given:
        Burrito expected = fixture.beefLettuceBurrito();

        //when:
        Optional<Burrito> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyBurrito(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectResults() {
        //given:
        List<Burrito> expectedBurritos = fixture.burritos();

        //when:
        List<Burrito> results = repository.findAll();

        //then:
        verifier.verifyBurritos(expectedBurritos, results);
    }

    @Test
    void create_shouldCreateEntriesForBurritoAndIngredients() {
        //given:
        Burrito chickenSalsaBurrito = Burrito.builder().name("chicken_salsa").ingredients(List.of(fixture.chicken().getId(), fixture.salsa().getId())).build();

        //when:
        Burrito persisted = repository.create(chickenSalsaBurrito);

        //then:
        verifier.verifyBurritoWasPersistedCorrectly(persisted);
    }

    @Test
    void update_shouldThrowNullPointerException_whenIDIsNull() {
        //given:
        Burrito burrito = Burrito.builder().id(null).name("n").build();

        //when:
        ThrowingRunnable invocation = () -> repository.update(burrito);

        //then:
        assertThrows("ID is mandatory", NullPointerException.class, invocation);
    }
    
    @Test
    void update_shouldReturnFalse_andTakeNoAction_whenBurritoDoesNotExist() {
        //given:
        Burrito unknownBurrito = Burrito.builder().id(10290329L).name("unknown").build();
        
        //when:
        boolean updated = repository.update(unknownBurrito);

        //then:
        assertFalse(updated);
        verifier.verifyBurritoDoesNotExist(unknownBurrito.getId());
    }

    @Test
    void update_shouldUpdateBaseDetails_andAssociatedIngredients() {
        //given:
        Burrito toUpdate = fixture.beefLettuceBurrito();
        toUpdate.setName("new_name");
        toUpdate.setIngredients(List.of(fixture.chicken().getId()));

        //when:
        boolean updated = repository.update(toUpdate);

        //then:
        assertTrue(updated);
        verifier.verifyBurritoWasPersistedCorrectly(toUpdate);
    }

}