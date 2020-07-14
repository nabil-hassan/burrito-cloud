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
public class OrderRepositoryJPATest {

    private final OrderRepositoryJPA repository;
    private final JPARepositoryTestDataVerifier verifier;
    private final JdbcRepoTestFixture fixture;
    private final EntityConverter converter;

    @Autowired
    public OrderRepositoryJPATest(OrderRepositoryJPA repository, JPARepositoryTestDataVerifier verifier,
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
        Long invalidId = 10210L;

        //when:
        Optional<OrderJPA> resultOpt = repository.findById(invalidId);

        //then:
        assertTrue(resultOpt.isEmpty());
    }

    @Test
    public void findById_shouldReturnValue_whenRecordExists() {
        //given:
        OrderJPA expected = converter.toOrderJPA(fixture.chickenOrder());

        //when:
        Optional<OrderJPA> resultOpt = repository.findById(expected.getId());

        //then:
        assertTrue(resultOpt.isPresent());
        verifier.verifyOrder(expected, resultOpt.get());
    }

    @Test
    void findAll_shouldReturnCorrectValues() {
        //given:
        List<OrderJPA> orders = fixture.orders().stream().map(converter::toOrderJPA).collect(toList());

        //when:
        List<OrderJPA> results = Utils.toList(repository.findAll());

        //then:
        verifier.verifyOrders(orders, results);
    }

    @Test
    void save_shouldCreateNewEntity() {
        //given:
        OrderJPA order = OrderJPA.builder().name("on").street("st1").town("tw1").county("co1").postcode("PC1").creditCardNo("12345678").creditCardCCV("999").creditCardExpiryDate("0101").build();
        BurritoJPA burrito = converter.toBurritoJPA(fixture.chickenBurrito());
        order.getBurritos().add(burrito);

        //when:
        OrderJPA saved = repository.save(order);

        //then:
        assertNotNull(saved.getId());
        verifier.verifyOrderWasPersistedCorrectly(saved);
    }

    @Test
    void save_shouldUpdateExistingEntity() {
        //given:
        OrderJPA updatedOrder = converter.toOrderJPA(fixture.chickenOrder());
        updatedOrder.setName("NameIsUpdated");
        updatedOrder.setCreditCardNo("99999999");
        updatedOrder.setBurritos(List.of(converter.toBurritoJPA(fixture.beefLettuceBurrito())));

        //when:
        repository.save(updatedOrder);

        //then:
        verifier.verifyOrderWasPersistedCorrectly(updatedOrder);
    }

    @Test
    void delete_shouldRemoveEntity() {
        //given:
        OrderJPA toDelete = converter.toOrderJPA(fixture.chickenOrder());

        //when:
        repository.delete(toDelete);

        //then:
        verifier.verifyOrderDoesNotExist(toDelete.getId());
    }
}
