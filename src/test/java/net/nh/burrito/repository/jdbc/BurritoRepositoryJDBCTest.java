package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.repository.JDBCRepositoryTestDataVerifier;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class BurritoRepositoryJDBCTest {

    private final BurritoRepositoryJDBC repository;
    private final JDBCRepositoryTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;

    @Autowired
    public BurritoRepositoryJDBCTest(BurritoRepositoryJDBC repository, JDBCRepositoryTestDataVerifier verifier,
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
        Optional<BurritoJDBC> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectBurrito_whenRowExists() {
        //given:
        BurritoJDBC expected = fixture.beefLettuceBurrito();

        //when:
        Optional<BurritoJDBC> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyBurrito(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectResults() {
        //given:
        List<BurritoJDBC> expectedBurritos = fixture.burritos();

        //when:
        List<BurritoJDBC> results = repository.findAll();

        //then:
        verifier.verifyBurritos(expectedBurritos, results);
    }

    @Test
    void create_shouldCreateEntriesForBurritoAndIngredients() {
        //given:
        BurritoJDBC chickenSalsaBurrito = BurritoJDBC.builder().name("chicken_salsa").ingredients(List.of(fixture.chicken().getId(), fixture.salsa().getId())).build();

        //when:
        BurritoJDBC persisted = repository.create(chickenSalsaBurrito);

        //then:
        verifier.verifyBurritoWasPersistedCorrectly(persisted);
    }

    @Test
    void update_shouldThrowNullPointerException_whenIDIsNull() {
        //given:
        BurritoJDBC burrito = BurritoJDBC.builder().id(null).name("n").build();

        //when:
        ThrowingRunnable invocation = () -> repository.update(burrito);

        //then:
        assertThrows("ID is mandatory", NullPointerException.class, invocation);
    }
    
    @Test
    void update_shouldReturnFalse_andTakeNoAction_whenBurritoDoesNotExist() {
        //given:
        BurritoJDBC unknownBurrito = BurritoJDBC.builder().id(10290329L).name("unknown").build();
        
        //when:
        boolean updated = repository.update(unknownBurrito);

        //then:
        assertFalse(updated);
        verifier.verifyBurritoDoesNotExist(unknownBurrito.getId());
    }

    @Test
    void update_shouldUpdateBaseDetails_andAssociatedIngredients() {
        //given:
        BurritoJDBC toUpdate = fixture.beefLettuceBurrito();
        toUpdate.setName("new_name");
        toUpdate.setIngredients(List.of(fixture.chicken().getId()));

        //when:
        boolean updated = repository.update(toUpdate);

        //then:
        assertTrue(updated);
        verifier.verifyBurritoWasPersistedCorrectly(toUpdate);
    }

    @Test
    void delete_shouldReturnFalse_whenNoRowExists() {
        //given:
        Long unknownId = 202030L;

        //when:
        boolean deleted = repository.delete(unknownId);

        //then:
        assertFalse(deleted);
    }

    @Test
    void delete_shouldReturnTrue_removeBurritoAndAssociatedIngredients_whenRowExists() {
        //given:
        BurritoJDBC toDelete = fixture.unattachedBurrito();

        //when:
        boolean deleted = repository.delete(toDelete.getId());

        //then:
        assertTrue(deleted);
        verifier.verifyBurritoDoesNotExist(toDelete.getId());
    }

}