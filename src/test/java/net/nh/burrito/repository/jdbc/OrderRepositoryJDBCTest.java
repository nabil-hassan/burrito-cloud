package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
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

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class OrderRepositoryJDBCTest {

    private final OrderRepositoryJDBC repository;
    private final JDBCRepositoryTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;

    @Autowired
    public OrderRepositoryJDBCTest(OrderRepositoryJDBC repository, BurritoRepositoryJDBC burritoRepository, JDBCRepositoryTestDataVerifier verifier, JdbcRepoTestFixture fixture) {
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
        Optional<OrderJDBC> result = repository.findById(nonExistentId);

        //then:
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnCorrectBurrito_whenRowExists() {
        //given:
        OrderJDBC expected = fixture.chickenOrder();

        //when:
        Optional<OrderJDBC> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyOrder(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectResults() {
        //given:
        List<OrderJDBC> expected = fixture.orders();

        //when:
        List<OrderJDBC> results = repository.findAll();

        //then:
        verifier.verifyOrders(expected, results);
    }

    @Test
    void create_shouldCreateEntriesForBurritoAndIngredients() {
        //given:
        OrderJDBC newOrder = OrderJDBC.builder().orderName("new_order").street("street1").town("town1").county("county1").postcode("postcode1").creditCardNo("12345678")
                .creditCardExpiryDate("0304").creditCardCCV("670").burritos(List.of(fixture.chickenBurrito(), fixture.beefLettuceBurrito())).build();

        //when:
        OrderJDBC persisted = repository.create(newOrder);

        //then:
        verifier.verifyOrderWasPersistedCorrectly(persisted);
    }

    @Test
    void update_shouldThrowNullPointerException_whenIDIsNull() {
        //given:
        OrderJDBC orderWithNullId = OrderJDBC.builder().id(null).build();

        //when:
        ThrowingRunnable invocation = () -> repository.update(orderWithNullId);

        //then:
        assertThrows("ID is mandatory", NullPointerException.class, invocation);
    }

    @Test
    void update_shouldReturnFalse_andTakeNoAction_whenBurritoDoesNotExist() {
        //given:
        OrderJDBC unknownOrder = OrderJDBC.builder().id(10290329L).build();

        //when:
        boolean updated = repository.update(unknownOrder);

        //then:
        assertFalse(updated);
        verifier.verifyBurritoDoesNotExist(unknownOrder.getId());
    }

    @Test
    void update_shouldUpdateBaseDetails_andAssociatedIngredients() {
        //given:
        OrderJDBC toUpdate = fixture.chickenOrder();
        toUpdate.setOrderName("new-" + toUpdate.getOrderName());
        toUpdate.setStreet("new-" + toUpdate.getStreet());
        toUpdate.setTown("new-" + toUpdate.getTown());
        toUpdate.setCounty("new-" + toUpdate.getCounty());
        toUpdate.setPostcode("XXX");
        toUpdate.setCreditCardNo("209080");
        toUpdate.setCreditCardCCV("568");
        toUpdate.setCreditCardExpiryDate("9999");

        BurritoJDBC chickenBurritoShell = BurritoJDBC.builder().id(fixture.chickenBurrito().getId()).build(); // only need to pass id to do the magic
        BurritoJDBC beefBurritoShell = BurritoJDBC.builder().id(fixture.beefLettuceBurrito().getId()).build();
        toUpdate.setBurritos(List.of(chickenBurritoShell, beefBurritoShell));

        //when:
        boolean updated = repository.update(toUpdate);

        //then:
        assertTrue(updated);
        verifier.verifyOrderWasPersistedCorrectly(toUpdate);
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
        OrderJDBC toDelete = fixture.chickenOrder();

        //when:
        boolean deleted = repository.delete(toDelete.getId());

        //then:
        assertTrue(deleted);
        verifier.verifyOrderDoesNotExist(toDelete.getId());
    }

}