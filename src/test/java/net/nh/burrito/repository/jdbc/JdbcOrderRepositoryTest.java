package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Order;
import org.junit.Assert;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class JdbcOrderRepositoryTest {

    private final JdbcOrderRepository repository;
    private final JdbcRepoTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;

    @Autowired
    public JdbcOrderRepositoryTest(JdbcOrderRepository repository, JdbcBurritoRepository burritoRepository, JdbcRepoTestDataVerifier verifier, JdbcRepoTestFixture fixture) {
        this.repository = repository;
        this.verifier = verifier;
        this.fixture = fixture;
    }

    @BeforeEach
    void setUp() {
        fixture.initialiseData();
    }

    @Test
    public void findById_shouldReturnEmptyOptional_whenSpecifiedOrderDoesNotExist() {
        //given:
        Long nonExistentId = 1832829L;

        //when:
        Optional<Order> result = repository.findById(nonExistentId);

        //then:
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectBurrito_whenRowExists() {
        //given:
        Order expected = fixture.chickenOrder();

        //when:
        Optional<Order> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyOrder(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectResults() {
        //given:
        List<Order> expected = fixture.orders();

        //when:
        List<Order> results = repository.findAll();

        //then:
        verifier.verifyOrders(expected, results);
    }

    @Test
    void create_shouldCreateEntriesForBurritoAndIngredients() {
        //given:
        Order newOrder = Order.builder().orderName("new_order").street("street1").town("town1").county("county1").postcode("postcode1").creditCardNo("12345678")
                .creditCardExpiryDate("0304").creditCardCCV("670").burritos(List.of(fixture.chickenBurrito(), fixture.beefLettuceBurrito())).build();

        //when:
        Order persisted = repository.create(newOrder);

        //then:
        verifier.verifyOrderWasPersistedCorrectly(persisted);
    }

    @Test
    void update_shouldThrowNullPointerException_whenIDIsNull() {
        //given:
        Order orderWithNullId = Order.builder().id(null).build();

        //when:
        ThrowingRunnable invocation = () -> repository.update(orderWithNullId);

        //then:
        assertThrows("ID is mandatory", NullPointerException.class, invocation);
    }

    @Test
    void update_shouldReturnFalse_andTakeNoAction_whenBurritoDoesNotExist() {
        //given:
        Order unknownOrder = Order.builder().id(10290329L).build();

        //when:
        boolean updated = repository.update(unknownOrder);

        //then:
        assertFalse(updated);
        verifier.verifyBurritoDoesNotExist(unknownOrder.getId());
    }

    @Test
    void update_shouldUpdateBaseDetails_andAssociatedIngredients() {
        //given:
//        Burrito toUpdate = fixture.beefLettuceBurrito();
//        toUpdate.setName("new_name");
//        toUpdate.setIngredients(List.of(fixture.chicken().getId()));

        //when:
//        boolean updated = repository.update(toUpdate);

        //then:
//        assertTrue(updated);
//        verifier.verifyBurritoWasPersistedCorrectly(toUpdate);
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
        Order toDelete = fixture.chickenOrder();

        //when:
        boolean deleted = repository.delete(toDelete.getId());

        //then:
        assertTrue(deleted);
        verifier.verifyOrderDoesNotExist(toDelete.getId());
    }

}