package net.nh.burrito.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class JdbcOrderRepositoryTest {

    private final JdbcOrderRepository repository;
    private final JdbcRepoTestDataVerifier dataVerifier;
    private final JdbcRepoTestFixture testFixture;

//    private static final Order exampleOrder;
//    static {
//        exampleOrder = Order.builder().orderName("n1").street("s1").town("t1").county("c1").postcode("p1")
//                .creditCardNo("ccn").creditCardExpiryDate("exd").creditCardCCV("ccv").build();
//    }

    @Autowired
    public JdbcOrderRepositoryTest(JdbcOrderRepository repository, JdbcBurritoRepository burritoRepository, JdbcRepoTestDataVerifier dataVerifier, JdbcRepoTestFixture testFixture) {
        this.repository = repository;
        this.dataVerifier = dataVerifier;
        this.testFixture = testFixture;
    }

    @BeforeEach
    void setUp() {
        testFixture.initialiseData();
    }



    @Test
    public void findById_shouldReturnEmptyOptional_whenSpecifiedOrderDoesNotExist() {
        //GIVEN
        Long nonExistentId = 1832829L;

        //WHEN
        Optional<Order> result = repository.findById(nonExistentId);

        //THEN
        result.isEmpty();
    }



    private void isOrderEqualToExpected(Long orderId, Order expected, Order actual) {
        assertEquals(orderId, actual.getId());
        assertEquals(expected.getOrderName(), actual.getOrderName());
        assertEquals(expected.getStreet(), expected.getStreet());
        assertEquals(expected.getTown(), actual.getTown());
        assertEquals(expected.getCounty(), expected.getCounty());
        assertEquals(expected.getPostcode(), expected.getPostcode());
        assertEquals(expected.getCreditCardNo(), expected.getCreditCardNo());
        assertEquals(expected.getCreditCardExpiryDate(), expected.getCreditCardExpiryDate());
        assertEquals(expected.getCreditCardCCV(), expected.getCreditCardCCV());

    }
}