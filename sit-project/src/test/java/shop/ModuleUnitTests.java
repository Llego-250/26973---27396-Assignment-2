package shop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Unit tests: each module tested alone (as in the UNIT TESTING using JUnit notes). */
public class ModuleUnitTests {

    private InventoryService inventory;

    @BeforeEach
    void setUp() {
        inventory = new InventoryService();
    }

    @Test
    void validCredentialsShouldAuthenticate() {
        assertTrue(new CustomerService().authenticate("john", "1234"), "john/1234 must log in");
    }

    @Test
    void wrongPasswordShouldNotAuthenticate() {
        assertFalse(new CustomerService().authenticate("john", "bad"), "wrong password must fail");
    }

    @Test
    void knownProductShouldReturnPrice() {
        assertEquals(800.00, new ProductService().getPrice("P100"), 0.001);
    }

    @Test
    void unknownProductShouldReturnZero() {
        assertEquals(0.0, new ProductService().getPrice("X"), 0.001);
    }

    @Test
    void stockShouldStartAtTen() {
        assertEquals(10, inventory.getStock());
    }

    @Test
    void reduceStockShouldDecreaseStock() {
        inventory.reduceStock(2);
        assertEquals(8, inventory.getStock());
    }

    @ParameterizedTest
    @CsvSource({"1,true", "10,true", "11,false", "15,false"})
    void checkStockShouldCompareWithAvailable(int qty, boolean expected) {
        assertEquals(expected, inventory.checkStock(qty));
    }

    @ParameterizedTest
    @CsvSource({"1600.0,true", "0.01,true", "0.0,false", "-5.0,false"})
    void paymentShouldAcceptOnlyPositiveAmounts(double amount, boolean expected) {
        assertEquals(expected, new PaymentService().processPayment(amount));
    }

    @Test
    void notificationShouldContainCustomerName() {
        assertEquals("Order confirmation sent to John", new NotificationService().sendConfirmation("John"));
    }
}
